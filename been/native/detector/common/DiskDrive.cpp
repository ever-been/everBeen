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

/*! \file DiskDrive.cpp
 *  \author Branislav Repcek
 *  \date 2. 12. 2005
 *
 *  \brief Implementation of methods in DiskDrive class.
 */

#include "DiskDrive.h"
#include "UnicodeString.h"

using namespace std;

namespace hwdet
{
    /*! \param disk_size Size of the disk in bytes.
     *  \param model_name Model name string of the disk.
     *  \param media Identification of the media type supported by the drive.
     *  \param device Name of device assigned to the drive by Windows.
     */
    DiskDrive::DiskDrive(unsigned long long disk_size, const String &model_name, const String &media, const String &device) :
    XMLElement(TXT("diskDrive"))
    {
        AddSubNode((model = new XMLValueElement< String >(TXT("model"), model_name)));
        AddSubNode((device_name = new XMLValueElement< String >(TXT("deviceName"), device)));
        AddSubNode((size = new XMLValueElement< unsigned long long >(TXT("size"), disk_size)));
        AddSubNode((media_type = new XMLValueElement< String >(TXT("mediaType"), media)));
    }
    
    /*! \return Size of the drive in bytes.
     */
    unsigned long long DiskDrive::GetSize(void) const
    {
        return size->GetNodeValue();
    }
    
    /*! \return String with model name of the drive.
     */
    String DiskDrive::GetModelName(void) const
    {
        return model->GetNodeValue();
    }
    
    /*! return Media type identification string.
     */
    String DiskDrive::GetMediaType(void) const
    {
        return media_type->GetNodeValue();
    }
    
    /*! return Name fo the device assigned to the drive.
     */
    String DiskDrive::GetDeviceName(void) const
    {
        return device_name->GetNodeValue();
    }
} // namespace hwdet
