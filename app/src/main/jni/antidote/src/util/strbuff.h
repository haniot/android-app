/* -*- mode: C; c-file-style: "gnu"; indent-tabs-mode: nil; -*- */
/**
 * \file strbuff.h
 * \brief String Buffer header.
 *
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
 * \date Aug 3, 2010
 * \author Fabricio Silva
 */

#ifndef STRBUFF_H_
#define STRBUFF_H_

typedef struct StringBuffer {
	char *str;
	int size;
} StringBuffer;

StringBuffer *strbuff_new(int initial_size);
int strbuff_cat(StringBuffer *buf, char *str);
int strbuff_xcat(StringBuffer *buf, char *str);
void strbuff_del(StringBuffer *sb);



#endif /* STRBUFF_H_ */
