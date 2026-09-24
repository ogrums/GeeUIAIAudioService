# GeeUIAIAudioService

Android voice-assistant service for the GeeUI / Letianpai robot. It wakes on a keyword, recognizes speech, runs either a local skill (volume, music, alarm, motion) or a cloud LLM, then speaks the answer and drives robot gestures.

This document is the map for someone who has not read the code. Comments in source are English. Identifiers stay as they are in the tree.

## What the app does

1. Boot a foreground `Service` (`LTPAudioService`) that owns dialogue state.
2. Start wake-up and ASR through **iFlytek AIUI** and/or **DUI / Speechocean DDS**.
3. Route the transcript:
   - known skill (volume, media, reminder, gesture, face) -> local handler, no LLM;
   - free chat -> cloud model selected by `voiceType`.
4. Stream the model answer (SSE), split it into sentences, speak them with the DUI TTS engine, and play a speaking gesture.
5. Pull LLM keys from the robot backend (`Api.getAiConfig`) and rebuild the AI provider when the active model changes.

`AIUIAudioService` is an empty stub. Real work lives in `LTPAudioService`.

## Modules

| Module | Path | Role |
|---|---|---|
| app | `app/src/main/java/com/rhj/` | Service, UI, HTTP, gestures |
| audio | `audio/.../com/rhj/audio/` | DUI DDS wrapper (`RhjAudioManager`) |
| aiui | `aiui/.../com/geeui/aiui/` | iFlytek AIUI wrapper (`AIUIAudioManager`) |
| player | `player/.../com/rhj/player/` | Music playback |
| message | `message/.../com/rhj/message/` | Callback payload types |
| FmodSound | `FmodSound/` | Optional voice-effect native library |
| AI ports | `app/.../com/rhj/audio/ai/` | Swappable cloud AI (`AiProvider`) |

Vendor trees (FMOD C headers, iFlytek JARs, `libaar`) are third-party. Do not treat their comments as product docs.

## Runtime flow

```text
mic
  -> WakeWordDetector (AIUI IVW or DUI wakeup)
  -> VAD (speech begin / end)
  -> SpeechToText (ASR)
  -> NluEngine + SkillRouter
       | skill hit  -> local command (MCU, player, volume, alarm)
       | no skill   -> LlmChat.stream (SSE)
  -> sentence split in LTPAudioService
  -> TextToSpeech (RhjAudioManager.speak)
  -> gesture (GestureCenter) + expression
```

Dialogue states (`AiDialogueState`) match the DUI avatar values:

| State | Meaning |
|---|---|
| `IDLE` | Not awake, or the turn is finished (`avatar.silence`) |
| `LISTENING` | ASR running (`avatar.listening`) |
| `UNDERSTANDING` | Waiting for NLU or LLM (`avatar.understanding`) |
| `SPEAKING` | TTS playing (`avatar.speaking`) |
| `STANDBY` | Remote-control multi-turn, not finished (`avatar.standby`) |

## Cloud model switch

Remote config is `AIModel` (`app/.../com/rhj/speech/model/AIMdoel.kt`): `api_key`, `api_secret`, `app_id`, `voice_type`, `is_aide`.

`LTPAudioService.getAIConfiguration()` calls `SpeechDataRepository` -> `Api.getAiConfig`. The row with `isAide == 1` and `voiceType != "sys"` becomes the active model.

| `voiceType` | Provider id | Old function | HTTP shape |
|---|---|---|---|
| `xh`, `wx` | `spark` | `sendToAiXFAndWX` / `getFormXFAndWX` | GET SSE, query `aide`, `ts`, `q`, `sn` |
| `other`, `other4`, `sysGpt` | `openai` | `sendToChatGpt` / `getFormChatgpt` | GET SSE, query `openai_key`, `q`, `sn` |
| anything else | `spark` | same as `xh` | same as Spark |

Placeholder hosts in code today: `https://yourdomain.com/apipath` (Spark) and `http://yourdomain/apipath` (OpenAI proxy). Replace them in `AiRuntimeConfig`, not inside the service.

`AiProviderFactory.create(model)` builds the provider. `dealAiRequest` should call `aiProvider.turns.handleUtterance(...)` (or `aiProvider.llm.stream(...)`) instead of the `when (voiceType)` block.

## Functions that matter

### `LTPAudioService` (`app/.../service/LTPAudioService.kt`)

| Function | Called when | What it does |
|---|---|---|
| `onCreate` / `onStartCommand` | process start | `init`, audio, wifi, charging |
| `init` | startup | config, callbacks, `initAudio` |
| `initAudio` | after init | DDS / AIUI, TTS and DM listeners |
| `dealAiRequest` | free-chat utterance | new UUID, then LLM |
| `getAIConfiguration` | wifi up / refresh | loads `AIModel`, sets `voiceType` and `aiProvider` |
| `readSmartResult` | a speakable clause is ready | gesture + `RhjAudioManager.speak` |
| `speak` paths via binder | `speakText` command | `RhjAudioManager.speak(data)` |
| `connectService` | startup | binds player, launcher, MCU (`ILetianpaiService`) |
| `responseGestureData` | skill or dance | sends motion / antenna / face |
| `uiEnterPause` | activity hidden | pause UI-driven dialogue |

Stale-turn guard: `lastAiWorkID` is compared to the request UUID. Tokens for an older id are dropped.

### `RhjAudioManager` (`audio/.../RhjAudioManager.java`)

DUI DDS agent.

| Function | What it does |
|---|---|
| DDS init + auth listeners | report init / auth success |
| `enableWakeup` / `disableWakeup` | wake engine and keyword |
| `speak(text)` / `speak(text, ttsId)` / `speak(..., priority)` | TTS; priority 0 is reserved for DDS |
| `shutupTts` | stop speech |
| `updateWkupRouter("dialog" \| "partner")` | auto-enter dialogue or hand the wake result to the partner |

### `AIUIAudioManager` (`aiui/.../AIUIAudioManager.kt`)

| Function | What it does |
|---|---|
| `initAudio` | load `cfg/aiui_phone.cfg`, set device SN, `AIUIAgent.createAgent` |
| `mAIUIListener` | `EVENT_WAKEUP`, `EVENT_RESULT` (iat), `EVENT_STATE`, `EVENT_TTS`, `EVENT_VAD`, `EVENT_SLEEP` |
| `speakText` | `CMD_TTS` with voice `x4_lingxiaoying_em_v2` |
| `shutupTTS` | `CMD_STOP` |
| `startRecord` / `stopRecordAudio` | 16 kHz PCM into AIUI |
| `dealIatResult` | join iFlytek IAT words into the final transcript |

### HTTP (`app/.../speech/http/`)

| Symbol | What it does |
|---|---|
| `Api.getAiConfig` | GET LLM list |
| `Api.getWakeConfig` | GET wake-word config |
| `Api.getConfigData` | GET dance / sound config |
| `Api.uploadLog` / `uploadCall` | POST diagnostics |
| `LTPHeaderInterceptor` | adds device headers (MAC when present) |
| `SpeechDataRepository` | Retrofit calls used by the service |

### `com.rhj.audio.ai` ports

Swap a cloud backend by implementing these and returning them from `AiProviderFactory`. Do not add another `when (voiceType)` in the service.

| Type | Contract |
|---|---|
| `AiProvider` | facade: `stt`, `llm`, `tts`, `wakeWord`, `vad`, `nlu`, `skills`, `capture`, `turns` |
| `AiRuntimeConfig` | `providerId`, `baseUrl`, keys, `voiceType`; `from(AIModel)` |
| `AiProviderFactory` | `create(AIModel)` / `create(AiRuntimeConfig)` / `createDefault()` |
| `SpeechToText` | `start(onPartial, onFinal)`, `stop` |
| `LlmChat` | `stream(prompt, sessionId, TokenListener)` |
| `TokenListener` | `onOpen`, `onToken`, `onComplete`, `onError` |
| `TextToSpeech` | `speak`, `stop` |
| `TtsPlaybackListener` | begin / progress / end / error |
| `WakeWordDetector` + `WakeWordListener` | keyword + optional DOA degrees |
| `VoiceActivityDetector` + `VadListener` | speech begin, end, timeout |
| `NluEngine` + `NluResult` | intent, skill id, slots |
| `SkillRouter` + `SkillHandler` | claim and run a local skill |
| `TurnController` | skill first, else LLM (`DefaultTurnController`) |
| `DialogueSession` | multi-turn `sendUserText`, `interrupt`, `end` |
| `AiEventListener` + `AiDialogueState` | UI / gesture state |
| `AudioCapture` + `AudioFrameListener` | PCM frames for a cloud STT |
| `AiConfigRepository` | `loadActiveConfig`, `loadModels` |
| `SseQueryLlmChat` | shared GET+SSE client (Spark and OpenAI query names) |
| `DuiTextToSpeech` | delegates to `RhjAudioManager` |
| `DefaultAiProvider` | wires the defaults above |

No-op classes (`NoOpSpeechToText`, `NoOpWakeWordDetector`, `NoOpVad`, `NoOpNluEngine`, `NoOpSkillRouter`, `NoOpAudioCapture`) exist so a provider can ship only an LLM. Wake-up today still belongs to AIUI / DUI until those ports are implemented for real.

## Message types (`MessageBean`)

| Constant | Meaning |
|---|---|
| `TYPE_INPUT` / `TYPE_OUTPUT` | user text / spoken reply |
| `TYPE_WIDGET_CONTENT` / `LIST` / `WEB` | rich cards |
| `TYPE_WIDGET_MEDIA` | music payload |
| `TYPE_WIDGET_WEATHER` | weather card |
| `TYPE_WAKEUP_RESULT` | wake event |
| `TYPE_VAD_TIMEOUT` | ASR timeout, empty audio |
| `TYPE_DIALOG_START` / `TYPE_DIALOG_END` | dialogue bounds |

## How to add another cloud API

1. Implement `LlmChat` (and `SpeechToText` / `TextToSpeech` only if that vendor replaces ASR or TTS).
2. Add a `providerId` branch in `AiProviderFactory.create`.
3. Map the new `voice_type` string in `AiRuntimeConfig.providerIdFor`.
4. Keep streaming. The robot speaks clause by clause and drops the turn if `sessionId` no longer matches `lastAiWorkID`.
5. Put the API key in a header when you control the client. The current proxy still sends `openai_key` as a query parameter; do not copy that into a new direct OpenAI client.
6. Leave volume, music, alarm, and motion on `SkillRouter`. Those must keep working if the LLM is down.

## Build

Gradle multi-module Android project. Root name in `settings.gradle` is `speech`. Several library projects are git submodules (`GeeUIBase`, `LtpNetWork`, `GeeUIComponents`); a fresh clone will not compile until those are checked out.

```text
./gradlew :app:assembleDebug
```

Signing material under `keystore/` is for the device build. Do not commit new secrets next to it.

## What is not in this service

- The wake-word acoustic model (binary assets under `aiui/src/main/assets`).
- Robot motion firmware (MCU service, separate app).
- The real LLM HTTP host (redacted as `yourdomain` in source).
