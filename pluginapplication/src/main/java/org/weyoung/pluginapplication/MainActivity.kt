package org.weyoung.pluginapplication

import android.os.Bundle
import android.view.LayoutInflater

internal class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(mContext).inflate(R.layout.activity_main, null).run {
            setContentView(this)
        }
    }
}
