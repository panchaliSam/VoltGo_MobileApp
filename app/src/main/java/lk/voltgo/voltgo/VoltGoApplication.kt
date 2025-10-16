/**
 * ------------------------------------------------------------
 * File: VoltGoApplication.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This is the main Application class for the VoltGo app.
 * It initializes the Hilt dependency injection framework,
 * enabling dependency management throughout the application lifecycle.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// The main Application class annotated with @HiltAndroidApp to initialize Dagger Hilt.
@HiltAndroidApp
class VoltGoApplication : Application()
