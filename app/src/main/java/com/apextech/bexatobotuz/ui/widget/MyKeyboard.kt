package com.apextech.bexatobotuz.ui.widget

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import com.apextech.bexatobotuz.R
import com.apextech.bexatobotuz.databinding.KeyboardLayoutBinding

class MyKeyboard : InputMethodService() {

    override fun onCreateInputView(): View {
        val keyboard = KeyboardLayoutBinding.inflate(layoutInflater)

        val buttonIds = arrayOf(
            R.id.btn0,
            R.id.btn,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
            R.id.btnQ,
            R.id.btnW,
            R.id.btnE,
            R.id.btnR,
            R.id.btnT,
            R.id.btnY,
            R.id.btnU,
            R.id.btnI,
            R.id.btnO,
            R.id.btnP,
            R.id.btnA,
            R.id.btnS,
            R.id.btnD,
            R.id.btnF,
            R.id.btnG,
            R.id.btnH,
            R.id.btnJ,
            R.id.btnK,
            R.id.btnL,
            R.id.btnZ,
            R.id.btnX,
            R.id.btnC,
            R.id.btnV,
            R.id.btnB,
            R.id.btnN,
            R.id.btnM,
            R.id.btnDot,
            R.id.btnVergul,
        )

        buttonIds.forEach {
            val button = keyboard.root.findViewById<Button>(it)
            button.setOnClickListener {
                val inputConnection = currentInputConnection
                inputConnection.commitText(button.text.toString(), 1)

            }
        }

        keyboard.btnBack.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            return@setOnClickListener
        }
        keyboard.btnEnter.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            return@setOnClickListener
        }
        keyboard.btnSpace.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE))
            return@setOnClickListener
        }

        return keyboard.root
    }
}