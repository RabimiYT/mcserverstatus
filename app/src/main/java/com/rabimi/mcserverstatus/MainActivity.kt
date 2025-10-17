package com.rabimi.mcserverstatus

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkModeToggle = findViewById<ImageButton>(R.id.darkModeToggle)
        val addServerButton = findViewById<ImageButton>(R.id.addServerButton)

        // 🌙 保存されたモード状態を反映
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle, animate = false)

        // 🌞 ダーク/ライト切替（フェードアニメ付き）
        darkModeToggle.setOnClickListener {
            isDarkMode = !isDarkMode
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            updateToggleIcon(darkModeToggle, animate = true)
        }

        // 🧱 サーバー追加ボタン（Snackbar付き）
        addServerButton.setOnClickListener {
            // Snackbar表示
            Snackbar.make(it, "サーバーを追加しました！", Snackbar.LENGTH_SHORT).show()

            // 将来的に追加する処理例：
            // serverList.add(Server("Hypixel", "mc.hypixel.net"))
            // serverList.add(Server("Minemen (AS)", "as.minemen.club"))
            // adapter.notifyDataSetChanged()
        }
    }

    // 🌗 アイコン切り替え + フェードアニメーション
    private fun updateToggleIcon(button: ImageButton, animate: Boolean) {
        val newIcon = if (isDarkMode) R.drawable.ic_moon else R.drawable.ic_sun
        if (animate) {
            val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 150 }
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 150 }

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    button.setImageResource(newIcon)
                    button.startAnimation(fadeIn)
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
            button.startAnimation(fadeOut)
        } else {
            button.setImageResource(newIcon)
        }
    }
}