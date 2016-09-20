/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
package com.google.code.gtkjfilechooser;

public class RemovableDevice extends BasicPath{

	private static final long serialVersionUID = 1L;

	public enum RemovableDeviceType {
		GNOME_DEV_HARDDISK_USB, GNOME_DEV_REMOVABLE_USB, GNOME_DEV_DISC_DVDROM, GNOME_DEV_MEDIA_SDMMC, GNOME_DEV_REMOVABLE;

		public String toIconName() {
			return toString().toLowerCase().replace('_', '-');
		}

		/**
		 * Returns the RemovableDeviceType.
		 * 
		 * <pre>
		 * sdb : drive-harddisk-usb.svg (esempio: harddisk esterno usb)
		 * sdc : drive-removable-media-usb.svg (esempio: stick usb)
		 * sr : media-optical.svg  (esempio: CD o DVD)
		 * mmc : media-flash-sd.svg (media-flash.svg)
		 * other : drive-removable-media.svg
		 * </pre>
		 * 
		 * @param dev
		 * @return
		 */
		public static RemovableDeviceType getType(String dev) {
			if (dev.startsWith("/dev/sdb")) {
				return GNOME_DEV_HARDDISK_USB;
			} else if (dev.startsWith("/dev/sdc")) {
				return GNOME_DEV_REMOVABLE_USB;
			} else if (dev.startsWith("/dev/sr")) {
				return GNOME_DEV_DISC_DVDROM;
			} else if (dev.startsWith("/dev/mmc")) {
				return GNOME_DEV_MEDIA_SDMMC;
			}

			return GNOME_DEV_REMOVABLE;
		}
	};


	private RemovableDeviceType type;

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public RemovableDeviceType getType() {
		return type;
	}

	public void setType(RemovableDeviceType type) {
		this.type = type;
	}

	@Override
	public String getIconName() {
		return "devices/" + type.toIconName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemovableDevice [name=");
		builder.append(name);
		builder.append(", location=");
		builder.append(location);
		builder.append(", type=");
		builder.append(type);
		builder.append(", iconName=");
		builder.append(getIconName());
		builder.append("]");
		return builder.toString();
	}

}
