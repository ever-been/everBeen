/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */

/*! \file NativeDetector.cpp
 *  \author Branislav Repcek
 *  \date 2005
 *
 * \brief Definitions of exported methods.
 */

#include <jni.h>
#include "../Common/UnicodeString.h"
#include "WindowsDetector.h"
#include "NativeDetector.h"

//! Detector instance.
hwdet::WindowsDetector detector;

/*! Initialize detector.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeInitialize(JNIEnv *, jobject) {

    return jboolean(detector.Initialize());
}

/*! Detect hardware/software features.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeExecute(JNIEnv *, jobject) {

    return jboolean(detector.Detect());
}

/*! Get data retrieved by detector.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetData(JNIEnv *env, jobject) {

    ::hwdet::StringStream stream;

    detector.WriteToStream(stream);

#ifndef UNICODE
    stream << TXT('\n'); // it seems that in ANSI mode one character is eaten from the end of the string...

    return env->NewStringUTF(stream.str().c_str());
#else
    return env->NewString((jchar *) stream.str().c_str(), (jsize) stream.str().length());
#endif
}

/*! Get messages string produced by the detector.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetMessages(JNIEnv *env, jobject) {

    ::hwdet::StringStream stream;

    detector.WriteMessagesToStream(stream);

#ifndef UNICODE
    stream << TXT('\n'); // it seems that in ANSI mode one character is eaten from the end of the string...
    return env->NewStringUTF(stream.str().c_str());
#else
    return env->NewString((jchar *) stream.str().c_str(), (jsize) stream.str().length());
#endif
}

/*! Clean-up.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeDestroy(JNIEnv *, jobject) {

    return jboolean(detector.Destroy());
}

/*! Get encoding used in the library.
 */
JNIEXPORT jstring JNICALL Java_cz_cuni_mff_been_task_detector_NativeDetector_nativeGetEncoding(JNIEnv *env, jobject) {

#ifndef UNICODE
    return env->NewStringUTF((detector.GetOutputEncoding() + '\n').c_str());
#else
    return env->NewString((jchar *) detector.GetOutputEncoding().c_str(), (jsize) detector.GetOutputEncoding().length());
#endif
}
