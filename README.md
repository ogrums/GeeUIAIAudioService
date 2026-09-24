# GeeUIAIAudioService

Voice assistant for the GeeUI robot. It wakes on a keyword, recognizes speech, runs a local skill or a cloud LLM, speaks the answer, and asks the launcher and the MCU to play a gesture.

`AIUIAudioService` is an empty stub. The real service is `LTPAudioService`. Gradle project name is `speech`. There was no README in the upstream tree.

## Modules

| Module | Role |
|---|---|
| `app` | `LTPAudioService`, HTTP, gestures, UI |
| `audio` | DUI / Speechocean DDS wrapper `RhjAudioManager` |
| `aiui` | iFlytek AIUI wrapper `AIUIAudioManager` |
| `player` | Music playback |
| `message` | Callback payload types (`MessageBean`) |
| `FmodSound` | Optional FMOD playback of a TTS URL, with one fixed pitch effect |
| `libaar` | Vendored SDK archive |

Submodules pulled in by `settings.gradle`: `CommandLib`, `library`, `GestureFactory` from `GeeUIBase`; `LtpNetWork`; `CommChannel` and `Components` from `GeeUIComponets`.

## Runtime

```text
mic
  -> wake word (AIUI or DUI)
  -> VAD
  -> speech to text
  -> local skill (volume, media, alarm, motion) or cloud LLM
  -> sentence split
  -> RhjAudioManager.speak
  -> gesture (GestureCenter) and face
```

`dealAiRequest` starts a cloud turn and stores a UUID in `lastAiWorkID`. Tokens for an older id are dropped. `getAIConfiguration` loads `Api.getAiConfig` and keeps the row with `isAide == 1` and `voiceType != "sys"`.

| `voiceType` | Old function | Request |
|---|---|---|
| `xh`, `wx` | `sendToAiXFAndWX` | GET SSE, query `aide`, `ts`, `q`, `sn` |
| `other`, `other4`, `sysGpt` | `sendToChatGpt` | GET SSE, query `openai_key`, `q`, `sn` |
| anything else | same as Spark | same as `xh` |

The hosts in source are the placeholders `https://yourdomain.com/apipath` and `http://yourdomain/apipath`. `third_party_demo` is a local stand-in that speaks the same SSE shape.

`RhjAudioManager.speak` is the normal TTS path. `FmodSound.playTts` is used only when `useFmod` is true, and it plays `speakUrl` with a fixed pitch of 1.63. The older funny / uncle / robot modes in `native-lib.cpp` are commented out and are not in the CMake build.

## Functions to know

| Function | What it does |
|---|---|
| `LTPAudioService.initAudio` | Starts DDS or AIUI and the TTS listeners |
| `LTPAudioService.readSmartResult` | One speakable clause: gesture, then `speak` |
| `LTPAudioService.connectService` | Binds the player, the launcher, and the MCU (`ILetianpaiService`) |
| `RhjAudioManager.enableWakeup` / `speak` / `shutupTts` | Wake engine, TTS, stop |
| `AIUIAudioManager.initAudio` | Loads `cfg/aiui_phone.cfg` and creates the AIUI agent |
| `AIUIAudioManager.dealIatResult` | Joins iFlytek words into the transcript |
| `Api.getAiConfig` | LLM list from the robot backend |
| `Api.getWakeConfig` | Wake-word config |

`MessageBean` types: `TYPE_INPUT`, `TYPE_OUTPUT`, widget content / list / web / media / weather, wake-up, VAD timeout, dialogue start and end.

## Swapping the cloud API

A `com.rhj.audio.ai` port set (on the `feat/ai-provider` branch of the fork) replaces the `when (voiceType)` block with `AiProvider`: speech-to-text, LLM stream, TTS, wake word, VAD, NLU, skills, and a turn controller. Add a provider by implementing `LlmChat` and one branch in `AiProviderFactory`. Keep streaming, and keep local skills on the device so volume and motion still work if the LLM is down.

## Build

```text
./gradlew :app:assembleDebug
```

Submodules must be checked out or the Gradle includes fail. Signing material under `keystore/` is for the device build.
