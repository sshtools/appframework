/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.gtkjfilechooser;

public class RemovableDevice extends BasicPath{

	public enum RemovableDeviceType {
		GNOME_DEV_DISC_DVDROM, GNOME_DEV_HARDDISK_USB, GNOME_DEV_MEDIA_SDMMC, GNOME_DEV_REMOVABLE, GNOME_DEV_REMOVABLE_USB;

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
		 * @param dev device
		 * @return device type
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

		public String toIconName() {
			return toString().toLowerCase().replace('_', '-');
		}
	}

	private static final long serialVersionUID = 1L;;


	private RemovableDeviceType type;

	@Override
	public String getIconName() {
		return "devices/" + type.toIconName();
	}

	public RemovableDeviceType getType() {
		return type;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(RemovableDeviceType type) {
		this.type = type;
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
