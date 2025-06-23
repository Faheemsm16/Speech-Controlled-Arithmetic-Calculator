# Speech-Controlled-Arithmetic-Calculator

# Voice Command Calculator (Android)

## Overview

This Android app allows users to **perform arithmetic calculations using voice commands** or traditional button input. It uses:
- **Speech-to-Text (STT)** for interpreting math expressions via microphone
- **Text-to-Speech (TTS)** to read out the result
- A **simple calculator UI** with buttons for digits, operations, and brackets

> Supports basic math operations and recognizes keywords like “add”, “subtract”, “divide”, “multiply”, including trigonometric constants like “sin 30”.

---

## Features

- **Voice Recognition** to input math expressions  
- **TTS Result Speaking** (e.g., “Result is 24.5”)  
- Traditional **calculator button input**  
- Understands voice commands like:  
  - “two plus two”  
  - “ten divide by five”  
  - “sin 30 degrees”  

---

## Built With

- Language: **Java**
- IDE: **Android Studio**
- Core Libraries:
  - `TextToSpeech`
  - `RecognizerIntent`
  - [`exp4j`](https://www.objecthunter.net/exp4j/) (for expression evaluation)
- Minimum SDK: **API 21 (Lollipop)**

---

## How Voice Input Works

In `MainActivity.java`:

1. User taps 🎙️ `btnSpeak`  
2. Android's `RecognizerIntent` converts speech to text  
3. Phrases like:  
   - `"divide by"` → `/`  
   - `"into"` or `"x"` → `*`  
   - `"plus"` or `"add"` → `+`  
   - `"subtract"` → `-`  
4. Trigonometric expressions are mapped to approximated values (e.g., `sin 30 degrees` → `0.5`)  
5. Expression is parsed and evaluated using `ExpressionBuilder`  
6. Result is displayed and spoken via `TextToSpeech`

---

## How to Run

1. Open project in Android Studio  
2. Make sure your emulator or device has:  
   - Microphone permission  
   - TTS engine installed  
3. Run the app, tap the mic, and speak your expression  
4. See & hear the result instantly!

---

## Future Improvements

- Support **advanced functions**: `sin()`, `log()`, `√`, `π`, `e`
- Add **result history** panel
- **Dark mode toggle**
- Handle complex/nested expressions better

---

## Author

- **Mohammed Farhan S M** – `faheemproject20@gmail.com`

---

## License

This project is licensed under the **MIT License**
