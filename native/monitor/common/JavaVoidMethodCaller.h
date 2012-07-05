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

/*! \file JavaVoidMethodCaller.h
 *  \author Branislav Repcek
 *  \date 7. 10. 2006
 *
 *  \brief Header for class which provides wrapper for JNI method calls.
 */

#ifndef JAVA_VOID_METHOD_CALLER
#define JAVA_VOID_METHOD_CALLER

// Disable warnings about deprecated "unsecure" functions from STL in Visual Studio 2005+.
#if _MSC_VER >= 1400
#define _SCL_SECURE_NO_DEPRECATE 1
#endif

#include <jni.h>
#include <vector>
#include <algorithm>
#include "String.h"
#include "TypeList.h"
#include "CompileTimeCheck.h"

namespace lm
{
    /*! \brief Simple wrapper around calls to the void methods via JNI.
     *
     *  This class provides methods to call Java methods on some object with various types of parameters. Parameters
     *  of primitive types, strings and arrays of primitive types and strings are supported. Note that not all of the 
     *  combinations are supported, but they can be easily added. Currently, methods with following signatures are supported:
     *  <ul>
     *   <li>void methodName(&lt;type1&gt; param1)</li>
     *   <li>void methodName(&lt;type1&gt; param1, &lt;type2&gt; param2)</li>
     *   <li>void methodName(&lt;type1&gt; []param1)</li>
     *   <li>void methodName(&lt;type1&gt; []param1, &lt;type2&gt; []param2)</li>
     *  </ul>
     *  where &lt;type1&gt; or &lt;type2&gt; are primitive types in Java (boolean, int, short, long, float, double, byte, char) or strings.
     *  <br>
     *  For more details about JNI see JNI specification which can be found 
     *  <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jni/spec/jniTOC.html">here</a>.
     */
    class JavaVoidMethodCaller
    {
    public:

        /* Let the template meta-programming madness begin! 
         * 
         * This is also warning for those that are brave and try to read and understand this code.
         */

        /*! \brief Create method caller.
         *
         *  \param env Java environment from which library has been initialized.
         *  \param java_class Java class from which methods will be called.
         *  \param java_object Instance of the java_class on which methods will be called.
         *  \param swallow_java_exceptions If set to true, exception state will be cleared after each method call. 
         *         If set to false, exception state will not be cleared.
         */
        JavaVoidMethodCaller(JNIEnv *env, jclass java_class, jobject java_object, bool swallow_java_exceptions = true) :
        jni_env(env),
        target_class(java_class),
        object(java_object),
        swallow_exceptions(swallow_java_exceptions)
        {
        }

        /*! \brief Destructor.
         */
        ~JavaVoidMethodCaller(void)
        {
        }

        /*! \brief Call method without parameters.
         *
         *  \param method_name Name of the method to call. Name is case-sensitive.
         *  \param method_signature Signature string of the method (can be generated for example via <code>javap -s</code>).
         *
         *  \return true on success, false otherwise (if exception has been thrown or method was not found).
         *
         *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
         */
        bool Call(const char *method_name, const char *method_signature)
        {
            jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

            if (!method_id)
            {
                return false;
            }

            jni_env->CallVoidMethod(object, method_id);

            if (jni_env->ExceptionCheck())
            {
                if (swallow_exceptions)
                {
                    jni_env->ExceptionClear();
                }
                return false;
            }

            return true;
        }

        /*! \brief Call method with one parameter of primitive type.
         *
         *  \param method_name Name of the method to call. Name is case-sensitive.
         *  \param method_signature Signature string of the method (can be generated for example via <code>javap -s</code>).
         *  \param param_1 Parameter that will be passed to the Java method. Type of this parameter can be one of jchar, jbyte, jint, 
         *         jshort, jlong, jfloat, jdouble, jboolean. If type is invalid, error is reported during compilation via 
         *         COMPILE_TIME_CHECK macro.
         *
         *  \return true on success, false otherwise (if exception has been thrown or method was not found).
         *
         *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
         */
        template< typename T1 > 
        bool Call(const char *method_name, const char *method_signature, T1 param_1)
        {
            const int type_index = ::tl::IndexOf< JavaTypes, T1 >::value;

            COMPILE_TIME_CHECK(type_index >= 0, Parameter_type_is_not_supported);

            jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

            if (!method_id)
            {
                return false;
            }

            jni_env->CallVoidMethod(object, method_id, param_1);

            if (jni_env->ExceptionCheck())
            {
                if (swallow_exceptions)
                {
                    jni_env->ExceptionClear();
                }
                return false;
            }

            return true;
        }

        /*! \brief Call method with two parameters of primitive types.
         *
         *  \param method_name Name of the method to call. Name is case-sensitive.
         *  \param method_signature Signature string of the method (can be generated for example via <code>javap -s</code>).
         *  \param param_1 First parameter that will be passed to the Java method. Parameter type can be one of jboolean, 
         *         jchar, jbyte, jint, jshort, jlong, jfloat, jdouble. If type is incorrect, error is reported during compilation
         *         via COMPILE_TIME_CHECK macro.
         *  \param param_2 Second parameter that will be passed to the Java method. Parameter type can be one of jboolean, 
         *         jchar, jbyte, jint, jshort, jlong, jfloat, jdouble. If type is incorrect, error is reported during compilation
         *         via COMPILE_TIME_CHECK macro.
         *
         *  \return true on success, false otherwise (if exception has been thrown or method was not found).
         *
         *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
         */
        template< typename T1, typename T2 > 
        bool Call(const char *method_name, const char *method_signature, T1 param_1, T2 param_2)
        {
            const int type_index_1 = ::tl::IndexOf< JavaTypes, T1 >::value;
            const int type_index_2 = ::tl::IndexOf< JavaTypes, T2 >::value;

            COMPILE_TIME_CHECK(type_index_1 >= 0, First_parameters_type_is_not_supported);
            COMPILE_TIME_CHECK(type_index_2 >= 0, Second_parameters_type_is_not_supported);

            jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

            if (!method_id)
            {
                return false;
            }

            jni_env->CallVoidMethod(object, method_id, param_1, param_2);

            if (jni_env->ExceptionCheck())
            {
                if (swallow_exceptions)
                {
                    jni_env->ExceptionClear();
                }
                return false;
            }

            return true;
        }

        /*! \brief Call method with one parameter of array of primitive type.
         *
         *  \param method_name Name of the method to call. Name is case-sensitive.
         *  \param method_signature Signature string of the method (can be generated for example via <code>javap -s</code>).
         *  \param param_1 <code>std::vector</code> which contains elements of the array. Elements can be one of the following types:
         *         jboolean, jchar, jbyte, jint, jshort, jlong, jfloat, jdouble. Separate specialization exists for string arrays.
         *         If the type of array's elements is invalid, error is reported via <code>COMPILE_TIME_CHECK</code> macro.
         *
         *  \return true on success, false otherwise (if exception has been thrown or method was not found).
         *
         *  \note If swallow_exceptions is set to true, JVM exception state will be cleared if exception has been thrown during method call.
         */
        template< typename T1 >
        bool Call(const char *method_name, 
                  const char *method_signature, 
                  const std::vector< T1 > &param_1)
        {
            const int type_index = ::tl::IndexOf< JavaTypes, T1 >::value;

            // Test of type is valid and it is not void
            COMPILE_TIME_CHECK(type_index >= 1, Parameter_type_is_not_supported);

            jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

            if (!method_id)
            {
                return false;
            }

            typedef typename ::tl::Get< ArrayMethodAdapters, type_index >::Result AdapterType;

            bool result = true;
            AdapterType adapter(jni_env);

            typename AdapterType::JavaType java_array = adapter.NewArray(static_cast< jsize >(param_1.size()));

            // Copy data from vector to the Java array.
            T1 *array_data = new T1[param_1.size()];

            std::copy(param_1.begin(), param_1.end(), array_data);

            adapter.SetArrayRegion(java_array, 0, static_cast< jsize >(param_1.size()), array_data);

            jni_env->CallVoidMethod(object, method_id, java_array);

            if (jni_env->ExceptionCheck())
            {
                if (swallow_exceptions)
                {
                    jni_env->ExceptionClear();
                }
                result = false;
            }

            adapter.DeleteArray(java_array, array_data); // <- so stupid, I want to free array without copying crap around
            //delete []array_data;

            return result;
        }

        /*! \brief Call method with two parameters of arrays of primitive types.
         *
         *  \param method_name Name of the method to call. Name is case-sensitive.
         *  \param method_signature Signature string of the method (can be generated for example via <code>javap -s</code>).
         *  \param param_1 <code>std::vector</code> which contains elements of the first array. Elements can be one of the following types:
         *         jboolean, jchar, jbyte, jint, jshort, jlong, jfloat, jdouble. 
         *         If the type of array's elements is invalid, error is reported via <code>COMPILE_TIME_CHECK</code> macro.
         *  \param param_2 <code>std::vector</code> which contains elements of the second array. Elements can be one of the following types:
         *         jboolean, jchar, jbyte, jint, jshort, jlong, jfloat, jdouble. 
         *         If the type of array's elements is invalid, error is reported via <code>COMPILE_TIME_CHECK</code> macro.
         *
         *  \return true on success, false otherwise (if exception has been thrown or method was not found).
         *
         *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
         */
        template< typename T1, typename T2 > 
        bool Call(const char *method_name, 
                  const char *method_signature, 
                  const std::vector< T1 > &param_1,
                  const std::vector< T2 > &param_2)
        {
            const int type_index_1 = ::tl::IndexOf< JavaTypes, T1 >::value;
            const int type_index_2 = ::tl::IndexOf< JavaTypes, T2 >::value;

            COMPILE_TIME_CHECK(type_index_1 >= 1, Parameter_type_is_not_supported);
            COMPILE_TIME_CHECK(type_index_2 >= 1, Parameter_type_is_not_supported);

            jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

            if (!method_id)
            {
                return false;
            }

            typedef typename ::tl::Get< ArrayMethodAdapters, type_index_1 >::Result AdapterType_1;
            typedef typename ::tl::Get< ArrayMethodAdapters, type_index_2 >::Result AdapterType_2;

            bool result = true;

            AdapterType_1 adapter_1(jni_env);
            AdapterType_2 adapter_2(jni_env);

            typename AdapterType_1::JavaType java_array_1 = adapter_1.NewArray(static_cast< jsize >(param_1.size()));
            typename AdapterType_2::JavaType java_array_2 = adapter_2.NewArray(static_cast< jsize >(param_2.size()));

            // Copy data from first vector to the first Java array.
            T1 *array_data_1 = new T1[param_1.size()];

            std::copy(param_1.begin(), param_1.end(), array_data_1);
            adapter_1.SetArrayRegion(java_array_1, 0, static_cast< jsize >(param_1.size()), array_data_1);

            // Copy data from second vector to the second Java array.
            T2 *array_data_2 = new T2[param_1.size()];

            std::copy(param_2.begin(), param_2.end(), array_data_2);
            adapter_2.SetArrayRegion(java_array_2, 0, static_cast< jsize >(param_2.size()), array_data_2);

            jni_env->CallVoidMethod(object, method_id, java_array_1, java_array_2);

            if (jni_env->ExceptionCheck())
            {
                if (swallow_exceptions)
                {
                    jni_env->ExceptionClear();
                }
                result = false;
            }

            adapter_1.DeleteArray(java_array_1, array_data_1);
            //delete []array_data_1;

            adapter_2.DeleteArray(java_array_2, array_data_2);
            //delete []array_data_2;

            return result;
        }

        /*! \brief Get flag for exception swallowing.
         *
         *  \return true if exceptions are swallowed during method calls, false otherwise. If this is set to true, exception
         *          state of the JVM is cleared if exception has been thrown during method call.
         */
        bool GetSwallowExceptions(void)
        {
            return swallow_exceptions;
        }

        /*! \brief Set flag for exception swallowing.
         *
         *  \param se New value of the flag. If this is set to true, exception state of the JVM is cleared if exception has been 
         *         thrown during method call.
         *
         *  \return Old state of the flag.
         */
        bool SetSwallowException(bool se)
        {
            bool ose = swallow_exceptions;

            swallow_exceptions = se;

            return ose;
        }

    private:
        //! Java environment.
        JNIEnv  *jni_env;

        //! Type of the object on which methods will be called.
        jclass  target_class;

        //! Instance of the Java object on which methods will be called.
        jobject object;

        //! Flag which specifies if exception state is cleared after call that throws.
        bool    swallow_exceptions;

        /*! \brief Typelist which contains all basic Java types.
         * 
         *  This list is used in all Call methods to verify types of the arguments in compilation-time.
         */
        typedef CREATE_TYPE_LIST_09(void, 
                                    jboolean, 
                                    jbyte, 
                                    jchar, 
                                    jshort, 
                                    jint, 
                                    jlong, 
                                    jfloat, 
                                    jdouble) JavaTypes;

        /*! \brief Typelist which contains all primitive array Java types.
         * 
         *  This list is used when selecting correct return type in ArrayMethodAdapter_### classes.
         */
        typedef CREATE_TYPE_LIST_09(void, 
                                    jbooleanArray, 
                                    jbyteArray, 
                                    jcharArray, 
                                    jshortArray, 
                                    jintArray, 
                                    jlongArray, 
                                    jfloatArray, 
                                    jdoubleArray) JavaArrayTypes;


        /*! Creates new adapter class which helps with array manipulations.
         */
#define CREATE_ARRAY_METHOD_ADAPTER_CLASS(NativeType_, middle_name)                                                                 \
        class ArrayMethodAdapter_##middle_name                                                                                      \
        {                                                                                                                           \
        public:                                                                                                                     \
            typedef NativeType_ NativeType;                                                                                         \
            typedef ::tl::Get< JavaArrayTypes, ::tl::IndexOf< JavaTypes, NativeType >::value >::Result JavaType;                    \
            ArrayMethodAdapter_##middle_name(JNIEnv *env) :                                                                         \
            jni_env(env)                                                                                                            \
            {                                                                                                                       \
            }                                                                                                                       \
            ~ArrayMethodAdapter_##middle_name(void)                                                                                 \
            {                                                                                                                       \
            }                                                                                                                       \
            JavaType NewArray(jsize size)                                                                                           \
            {                                                                                                                       \
                return jni_env->New##middle_name##Array(size);                                                                      \
            }                                                                                                                       \
            void DeleteArray(JavaType java_array, NativeType *elements)                                                             \
            {                                                                                                                       \
                jni_env->Release##middle_name##ArrayElements(java_array, elements, 0);                                              \
            }                                                                                                                       \
            void GetArrayRegion(JavaType java_array, jsize start, jsize len, NativeType *buffer)                                    \
            {                                                                                                                       \
                jni_env->Get##middle_name##ArrayRegion(java_array, start, len, buffer);                                             \
            }                                                                                                                       \
            void SetArrayRegion(JavaType java_array, jsize start, jsize len, NativeType *buffer)                                    \
            {                                                                                                                       \
                jni_env->Set##middle_name##ArrayRegion(java_array, start, len, buffer);                                             \
            }                                                                                                                       \
            NativeType *GetArrayElements(JavaType java_array)                                                                       \
            {                                                                                                                       \
                return jni_env->Get##middle_name##ArrayElements(java_array, NULL);                                                  \
            }                                                                                                                       \
            size_t GetArrayLength(jarray java_array)                                                                                \
            {                                                                                                                       \
                return jni_env->GetArrayLength(java_array);                                                                         \
            }                                                                                                                       \
        private:                                                                                                                    \
            JNIEnv  *jni_env;                                                                                                       \
        };

        /*! \brief Adapter class for string arrays in Java.
         *  
         *  This class contains methods to create and set elements of the JNI arrays of Java strings (jstring objects).
         */
        class ArrayMethodAdapter_String
        {
        public:
            //! Type of the array.
            typedef jobjectArray JavaType;

            //! Type of the array element.
            typedef jstring NativeType;

            /*! \brief Create new adapter.
             *
             *  \param env Pointer to the Java JNI environment. This cannot be NULL.
             */
            ArrayMethodAdapter_String(JNIEnv *env) :
            jni_env(env)
            {
                // Find string class from (java.lang.String).
                string_class = jni_env->FindClass("java/lang/String");
            }

            /*! \brief Destructor.
             */
            ~ArrayMethodAdapter_String(void)
            {
            }

            /*! \brief Create new array of strings.
             *
             *  \param size Length of the array.
             *
             *  \return Handle to the array of Java strings. All elements are set to <code>null</code>.
             */
            jobjectArray NewArray(jsize size)
            {
                return jni_env->NewObjectArray(size, string_class, NULL);
            }

            /*! \brief Set element at given index.
             *
             *  \param java_array Handle to the array (as returned by the NewArray method) in which elements has to be set.
             *  \param index Index of the element to set. This has to be smaller than length of the array.
             *  \param value Value to which element of the array should be set to.
             */
            void SetElement(jobjectArray java_array, size_t index, const String &value)
            {
#ifdef WIN32
                // Use UNICODE strings on Win
                jstring new_value = jni_env->NewString((jchar *) value.c_str(), static_cast< jsize >(value.length()));
#else
                // ASCII elsewhere
                jstring new_value = jni_env->NewStringUTF(value.c_str());
#endif

                jni_env->SetObjectArrayElement(java_array, static_cast< jsize >(index), new_value);
            }

            /*! \brief Get length of the array.
             *
             *  \param java_array Array to get length of.
             *
             *  \return Length of given array.
             */
            size_t GetArrayLength(jarray java_array)
            {
                return jni_env->GetArrayLength(java_array);
            }

        private:
            JNIEnv  *jni_env;
            jclass  string_class;
        };

        /*! \brief Adapter class for boolean array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jboolean, Boolean)

        /*! \brief Adapter class for byte array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jbyte, Byte)

        /*! \brief Adapter class for char array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jchar, Char)

        /*! \brief Adapter class for short array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jshort, Short)

        /*! \brief Adapter class for int array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jint, Int)

        /*! \brief Adapter class for long array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jlong, Long)

        /*! \brief Adapter class for float array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jfloat, Float)

        /*! \brief Adapter class for double array methods.
         */
        CREATE_ARRAY_METHOD_ADAPTER_CLASS(jdouble, Double)

        /*! \brief Typelist which contains all adapters for different types of arrays.
         *
         *  This typelist is used when new template instantiation of the array Call method is being created.
         *  During instantiation of the Call method correct adapter is selected based on the types of the parameters.
         */
        typedef CREATE_TYPE_LIST_09(void,
                                    ArrayMethodAdapter_Boolean,
                                    ArrayMethodAdapter_Byte,
                                    ArrayMethodAdapter_Char,
                                    ArrayMethodAdapter_Short,
                                    ArrayMethodAdapter_Int,
                                    ArrayMethodAdapter_Long,
                                    ArrayMethodAdapter_Float,
                                    ArrayMethodAdapter_Double) ArrayMethodAdapters;
    };

    /*! \brief Call method with one string parameter.
     *
     *  This is specialization of the Call function template for methods which take only one argument - Unicode string.
     *  String is automatically translated to the Java-compatible jstring object and that object is passed to the method
     *  as its only parameter.
     *
     *  \param method_name Name of the method to call. Name is case-sensitive.
     *  \param method_signature Signature string of the method (as reported by the <code>javap -s</code>).
     *  \param param_1 String that will be passed as the only argument.
     *
     *  \return true on success, false otherwise (method call failed or exception has been thrown).
     *
     *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
     */
    template< >
    bool JavaVoidMethodCaller::Call< String >(const char *method_name, const char *method_signature, String param_1)
    {
        jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

        if (!method_id)
        {
            return false;
        }

#ifdef WIN32
        jstring str_param = jni_env->NewString((jchar *) param_1.c_str(), static_cast< jsize >(param_1.length()));
#else
        jstring str_param = jni_env->NewStringUTF(param_1.c_str());
#endif

        jni_env->CallVoidMethod(object, method_id, str_param);

        if (jni_env->ExceptionCheck())
        {
            if (swallow_exceptions)
            {
                jni_env->ExceptionClear();
            }
            return false;
        }

        return true;
    }

    /*! \brief Call method with one parameter that is array of strings.
     *
     *  This is specialization of the Call function template for methods with one array parameter. This method works only with
     *  Unicode strings. Strings are automatically translated to the Java-compatible jstring objects.
     *
     *  \param method_name Name of the method to call. Name is case-sensitive.
     *  \param method_signature Signature string of the method to call (as reported by the <code>javap -s</code>).
     *  \param param_1 <code>std::pair</code> object which describes array of strings. First element of the pair contains
     *         elements of the array (that is, <code>std::wstring *</code>). Second element of the pair contains length
     *         of the array (number of strings in array).
     *
     *  \return true on success, false otherwise (method call failed or exception has been thrown).
     *
     *  \note If swallow_exceptions is set to true, exception state will be cleared if exception has been thrown during method call.
     */
    template< >
    bool JavaVoidMethodCaller::Call< String >(const char *method_name,
                                              const char *method_signature,
                                              const std::vector< String > &param_1)
    {
        jmethodID method_id = jni_env->GetMethodID(target_class, method_name, method_signature);

        if (!method_id)
        {
            return false;
        }

        ArrayMethodAdapter_String adapter(jni_env);

       /* Note: I have no idea if this creates memory leak or not. I was not able to find equivalent of the ReleaseXXXArrayElements
        * method which works for jobjectArrays. So I guess Java will do this automatically.
        */

        ArrayMethodAdapter_String::JavaType java_array = adapter.NewArray(static_cast< jsize >(param_1.size()));

        for (size_t i = 0; i < param_1.size(); ++i)
        {
            adapter.SetElement(java_array, i, param_1[i]);
        }

        jni_env->CallVoidMethod(object, method_id, java_array);

        if (jni_env->ExceptionCheck())
        {
            if (swallow_exceptions)
            {
                jni_env->ExceptionClear();
            }
            return false;
        }

        return true;
    }

} // namespace lm

#endif
