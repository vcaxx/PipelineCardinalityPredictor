/*
* Copyright 2008 Sun Microsystems, Inc.	All rights reserved.
* Use is subject to license terms.
*/#pragma ident "@(#)ipfwd_config.c 1.14		08/05/07 SMI"
#include "common.h"
#include "ipfwd_config.h"
/*
* This is the default configuration for NIU when running on LDoms environment.
* When using the 8 CORE config utilizing 64 CPUs, please ensure the
* ipfwd_swarch.c file enables 40 CPUs.
* Format: TID, openHandler, PORT, CHAN, ROLE, MEMPOOL, startQRX, #RxQ, startQTx, #TxQ
*/
#define NUM_OF_TCBS 1
ipfwd_thread_attr ipfwd_10g_niu_ldom_def[NUM_OF_TCBS][64] = {
{
{ 0, OPEN_OP, NXGE_10G_START_PORT, 0, TROLE_ETH_NETIF_RX, 0, 0, 1, 0, 1}, 
{ 1, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 2, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 3, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 4, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 5, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 6, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 7, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 8, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 9, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 10, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 11, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 12, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 13, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 14, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 15, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 16, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 17, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 18, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 19, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 20, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 21, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 22, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 23, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 24, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 25, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 26, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 27, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 28, 0, NXGE_10G_START_PORT, 0, TROLE_ETH_NETIF_TX, 0, 1, 1, 1, 1}, 
{ 29, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 30, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 31, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 32, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 33, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 34, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 35, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 36, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 37, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 38, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 39, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 40, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 41, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 42, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 43, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 44, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 45, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 46, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 47, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 48, 0, NXGE_10G_START_PORT, 0, TROLE_APP_IPFWD, 0, 0, 1, 1, 1}, 
{ 49, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 50, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 51, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 52, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 53, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 54, 0, 0, 0, -1, 0, 0, 0, 0, 0}, 
{ 55, 0, 0, 0, -1, 0, 0, 0, 0, 0}
}
};
/* NIU Configuration for platform setup for LDoms */
ipfwd_thread_attr *ipfwd_thread_config = (ipfwd_thread_attr *)&ipfwd_10g_niu_ldom_def[0][0];
