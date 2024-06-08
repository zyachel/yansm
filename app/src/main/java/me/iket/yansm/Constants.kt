package me.iket.yansm

import android.os.Build

const val linkToDemo = "https://codeberg.org/zyachel/yansm/src/branch/main/docs/USAGE.md"
const val linkToReadme = "https://codeberg.org/zyachel/yansm"
val helperTextForBelowAndroid13 = "Since your device is running on Android SDK ${Build.VERSION.SDK_INT}, you'll have to manually add the tile. Click on 'Watch demo' to see how to do it."
const val helperTextForAndroid13AndAbove = "Could not add tile. Please do so manually. Click on 'Watch demo' to see how to do it."
