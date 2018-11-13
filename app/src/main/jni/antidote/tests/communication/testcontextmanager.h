/**********************************************************************
 * Copyright (C) 2010 Signove Tecnologia Corporation.
 * All rights reserved.
 * Contact: Signove Tecnologia Corporation (contact@signove.com)
 *
 * $LICENSE_TEXT:BEGIN$
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation and appearing
 * in the file LICENSE included in the packaging of this file; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * $LICENSE_TEXT:END$
 *
 * testfsm.h
 *
 * Created on: Aug 27, 2010
 *     Author: fabricio.silva
 **********************************************************************/

#ifndef TESTCONTEXTMANAGER_H_
#define TESTCONTEXTMANAGER_H_

#ifdef TEST_ENABLED

#include "src/communication/context_manager.h"

void testctxmanager_add_suite();
void testctxmanager_test();


#endif /* TEST_ENABLED */

#endif /* TESTCONTEXTMANAGER_H_ */
