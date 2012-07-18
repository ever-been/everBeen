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

/*! \file LoadMonitorNative.cpp
 *  \author Branislav Repcek
 *  \date 5. 11. 2006
 *
 *  \brief Definitions of methods exported from the library.
 */

#include <jni.h>
#include <string>
#include <iostream>
#include "../common/String.h"
#include "../common/JavaVoidMethodCaller.h"
#include "../common/CompileTimeCheck.h"
#include "LoadMonitorNative.h"
#include "LoadMonitor.h"

using namespace std;

//! Load Monitor.
lm::LoadMonitor monitor;

/*! \brief Initialize Load Monitor.
 *  
 *  \return true if successfull, false otherwise.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeInitialize(JNIEnv *, jobject)
{
    return jboolean(monitor.Initialize());
}

/*! \brief Terminate Load Monitor and free all resources.
 *
 *  \return true if successfull, false otherwise.
 */
JNIEXPORT jboolean JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeTerminate(JNIEnv *, jobject)
{
    return jboolean(monitor.Terminate());
}

/*! \brief Take load sample and convert it to the Java-compatible class.
 *
 *  \param env Java environment.
 *
 *  \return Instance of <code>cz.cuni.mff.been.hostmanager.load.LoadSample</code> class which contains data from last 
 *          load measurement.
 */
JNIEXPORT jobject JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeGetSample(JNIEnv *env, jobject)
{
    COMPILE_TIME_CHECK(sizeof(jint) == sizeof(int), Invalid_integer_type_size);
    COMPILE_TIME_CHECK(sizeof(jlong) == sizeof(long long), Invalid_long_type_size);
    COMPILE_TIME_CHECK(sizeof(jshort) == sizeof(short), Invalid_short_type_size);

    lm::LoadSample sample(monitor.TakeSample());

    jclass sample_class = env->FindClass("cz/cuni/mff/been/hostmanager/load/LoadSample");

    if (!sample_class)
    {
        return NULL;
    }

    jobject sample_object = env->AllocObject(sample_class);

    if (!sample_object)
    {
        return NULL;
    }

    lm::JavaVoidMethodCaller method_caller(env, sample_class, sample_object);

    method_caller.Call("setTSC", "(J)V", static_cast< jlong >(sample.tsc));
    method_caller.Call("setTimeStamp", "(J)V", static_cast< jlong >(sample.sample_time));
    method_caller.Call("setMemoryFree", "(J)V", static_cast< jlong >(sample.memory_free));
    method_caller.Call("setProcessQueueLength", "(I)V", static_cast< jint >(sample.processor_queue));
    method_caller.Call("setProcessCount", "(I)V", static_cast< jint >(sample.process_count));
    method_caller.Call("setProcessorUsage", "([S)V", sample.processor_usage);
    method_caller.Call("setNetworkReadWriteSpeed", "([I[I)V", sample.network_bytes_in, sample.network_bytes_out);
    method_caller.Call("setDiskReadAndWriteBytes", "([J[J)V", sample.disk_read_bytes, sample.disk_write_bytes);
  
    return sample_object;
}

/*! \brief Get description of the hardware that is monitored by the library and return its Java representation.
 *  
 *  \param env Java JNI environment.
 *
 *  \return Instance of <code>cz.cuni.mff.been.hostmanager.load.HardwareDescription</code> class with description of the hardware.
 */
JNIEXPORT jobject JNICALL Java_cz_cuni_mff_been_hostmanager_load_LoadMonitorNative_nativeGetHardwareDescription(JNIEnv *env, jobject)
{
    lm::HardwareDescription description(monitor.GetHardwareDescription());

    jclass description_class = env->FindClass("cz/cuni/mff/been/hostmanager/load/HardwareDescription");

    if (!description_class)
    {
        return NULL;
    }

    jobject description_object = env->AllocObject(description_class);

    if (!description_object)
    {
        return NULL;
    }

    lm::JavaVoidMethodCaller method_caller(env, description_class, description_object, true);

    method_caller.Call("setTimeStamp", "(J)V", static_cast< jlong >(description.time));
    method_caller.Call("setDrives", "([Ljava/lang/String;)V", description.drives);
    method_caller.Call("setAdapters", "([Ljava/lang/String;)V", description.adapters);
    method_caller.Call("setCpuCount", "(S)V", static_cast< jshort >(description.processor_count));
    method_caller.Call("setMemorySize", "(J)V", static_cast< jlong >(description.memory_size));

    return description_object;
}
