# Copyright 2026 SadmanWasi
# SPDX-License-Identifier: Apache-2.0
#
# ProGuard / R8 configuration for Bangla Calendar (DinKhon)

-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-keepclassmembers class * {
    @androidx.compose.runtime.* <methods>;
}

# Preserve Kotlin enum values used in PreferencesRepository
-keepclassmembers enum com.banglacalendar.app.domain.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
