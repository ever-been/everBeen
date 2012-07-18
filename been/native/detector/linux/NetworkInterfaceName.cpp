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

/*! \file NetworkInterfaceName.cpp
 *  \author Branislav Repcek
 *  \date 12. 12. 2006
 * 
 *  \brief Translate HW id of the iface to the name.
 */

#include <net/if_arp.h>
#include "../common/UnicodeString.h"
#include "NetworkInterfaceName.h"

namespace hwdet
{
    /*! Get human readable name of the network interface.
     * 
     *  \param id Interface type as determined by the ioctl.
     * 
     *  \return String containing name of the network interface's type.
     */
    String GetInterfaceTypeByID(unsigned short id)
    {
        /* Keep this in sync with net/if_arp.h ! */
        switch (id)
        {
            case ARPHRD_NETROM:
                return TXT("NET/ROM pseudo");
                 
            case ARPHRD_ETHER:
                return TXT("Ethernet");
                
            case ARPHRD_EETHER:
                return TXT("Experimental Ethernet");
                
            case ARPHRD_AX25:
                return TXT("AX.25 Level 2");
                
            case ARPHRD_PRONET:
                return TXT("PROnet token ring");
                
            case ARPHRD_CHAOS:
                return TXT("Chaosnet");
                
            case ARPHRD_IEEE802:
                return TXT("IEEE 802.2 Ethernet");
                
            case ARPHRD_ARCNET:
                return TXT("ARCnet");
                
            case ARPHRD_APPLETLK:
                return TXT("APPLEtalk");
                
            case ARPHRD_DLCI:
                return TXT("Frame Relay DLCI");
                
            case ARPHRD_ATM:
                return TXT("ATM");
                
            case ARPHRD_METRICOM:
                return TXT("Metricom STRIP");
                
            case ARPHRD_IEEE1394:
                return TXT("IEEE 1394 IPv4");
                
            case ARPHRD_EUI64:
                return TXT("EUI-64");
                
            case ARPHRD_INFINIBAND:
                return TXT("InfiniBand");
                
            case ARPHRD_SLIP:
                return TXT("SLIP");
                
            case ARPHRD_CSLIP:
                return TXT("CSLIP");
                
            case ARPHRD_SLIP6:
                return TXT("SLIP6");    
                
            case ARPHRD_CSLIP6:
                return TXT("CSLIP6");
                
            case ARPHRD_RSRVD:
                return TXT("RSRVD");
                
            case ARPHRD_ADAPT:
                return TXT("ADAPT");
                
            case ARPHRD_ROSE:
                return TXT("ROSE");
                
            case ARPHRD_X25:
                return TXT("CCITT X.25");
                
            case ARPHRD_HWX25:
                return TXT("CCITT X.25 F");
                
            case ARPHRD_PPP:
                return TXT("PPP");
                
            case ARPHRD_CISCO:
                return TXT("Cisco HDLC");
                
            case ARPHRD_LAPB:
                return TXT("LAPB");
                
            case ARPHRD_DDCMP:
                return TXT("DDCMP");
                
            case ARPHRD_RAWHDLC:
                return TXT("Raw HDLC");
                
            case ARPHRD_TUNNEL:
                return TXT("IPIP tunnel");
                
            case ARPHRD_TUNNEL6:
                return TXT("IPIP6 tunnel");
                
            case ARPHRD_FRAD:
                return TXT("Frame Relay Access");
                
            case ARPHRD_SKIP:
                return TXT("SKIP");
                
            case ARPHRD_LOOPBACK:
                return TXT("Loopback");
                
            case ARPHRD_LOCALTLK:
                return TXT("Localtalk");
                
            case ARPHRD_FDDI:
                return TXT("Fiber Distributed Data Interface");
                
            case ARPHRD_BIF:
                return TXT("AP1000 BIF");
                        
            case ARPHRD_SIT:
                return TXT("IPv6-in-IPv4");
                
            case ARPHRD_IPDDP:
                return TXT("IP-in-DDP tunnel");
                
            case ARPHRD_IPGRE:
                return TXT("GRE over IP");
                
            case ARPHRD_PIMREG:
                return TXT("PIMSM register interface");
                
            case ARPHRD_HIPPI:
                return TXT("High Performance Parallel Interface");
                
            case ARPHRD_ASH:
                return TXT("Ash");
                
            case ARPHRD_ECONET:
                return TXT("Acorn Econet");
                
            case ARPHRD_IRDA:
                return TXT("IrDA");
                
            case ARPHRD_FCPP:
                return TXT("Point to point fibrechanel");
                
            case ARPHRD_FCAL:
                return TXT("Fibrechanel arbitrated loop");
                
            case ARPHRD_FCPL:
                return TXT("Fibrechanel public loop");
                
            case ARPHRD_FCFABRIC:
                return TXT("Fibrechanel fabric");
                
            case ARPHRD_IEEE80211:
                return TXT("IEEE 802.11");
                
            case ARPHRD_IEEE80211_PRISM:
                return TXT("IEEE 802.11 + Prism2 header");
                
            case ARPHRD_IEEE80211_RADIOTAP:
                return TXT("IEEE 802.11 + radiotap header");
        }
        
        return TXT("(unknown)");
    }
}
