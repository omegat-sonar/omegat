/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2008 Alex Buloichik
               2012 Didier Briel
               2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: https://omegat.org/support

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/
package org.omegat.util;

import java.awt.ComponentOrientation;

import org.omegat.core.Core;

public class BiDiUtils {

	public enum ORIENTATION {
		/** All text is left-to-right oriented. */
		ALL_LTR,
		/** All text is right-to-left oriented. */
		ALL_RTL,
		/**
		 * different texts/segments have different orientation, depending on
		 * language/locale.
		 */
		DIFFER
	};

	public static final String BIDI_LRE = "\u202a";
	public static final String BIDI_RLE = "\u202b";
	public static final String BIDI_PDF = "\u202c";
	public static final String BIDI_LRM = "\u200e";
	public static final String BIDI_RLM = "\u200f";
	public static final char BIDI_LRM_CHAR = '\u200e';
	public static final char BIDI_RLM_CHAR = '\u200f';
	// TODO: delete the dagger
	public static final String UNICODE_DAGGER = "\u2020";

	public static ORIENTATION getOrientationType() {
		ORIENTATION currentOrientation;

		if (Core.getProject().isProjectLoaded()) {
			currentOrientation = getOrientationFromProject();
		} else if (isLocaleRtl()) {
			// project not loaded, use locale
			currentOrientation = ORIENTATION.ALL_RTL;
		} else {
			// project not loaded, default to LTR
			currentOrientation = ORIENTATION.ALL_LTR;
		}
		return currentOrientation;
	}

	/**
	 * Decide what document orientation should be default for source/target
	 * languages.
	 */
	private static ORIENTATION getOrientationFromProject() {
		ORIENTATION currentOrientation;

		boolean sourceLangIsRTL = isSourceLangRtl();
		boolean targetLangIsRTL = isTargetLangRtl();

		if (sourceLangIsRTL) {
			currentOrientation = ORIENTATION.ALL_RTL;
		} else {
			currentOrientation = ORIENTATION.ALL_LTR;
		}
		if (sourceLangIsRTL != targetLangIsRTL || sourceLangIsRTL != isLocaleRtl()) {
			currentOrientation = ORIENTATION.DIFFER;
		}
		return currentOrientation;
	}

	/**
	 * Decide what document orientation should be default for source/target
	 * languages.
	 */
	public static ComponentOrientation getInitialOrientation() {
		return getOrientation(null);
	}

	/**
	 * Decide what document orientation should be default for source/target
	 * languages.
	 */
	public static ComponentOrientation getOrientation(ORIENTATION currentOrientation) {

		ComponentOrientation targetOrientation = null;

		if (currentOrientation == null) {
			currentOrientation = getOrientationType();
		}

		switch (currentOrientation) {
		case ALL_LTR:
			targetOrientation = ComponentOrientation.LEFT_TO_RIGHT;
			break;
		case ALL_RTL:
			targetOrientation = ComponentOrientation.RIGHT_TO_LEFT;
			break;
		case DIFFER:
			if (isTargetLangRtl()) {
				// using target lang direction gives better result when user
				// starts editing.
				targetOrientation = ComponentOrientation.RIGHT_TO_LEFT;
			} else {
				targetOrientation = ComponentOrientation.LEFT_TO_RIGHT;
			}
		}
		return targetOrientation;
	}

	public static String addRtlBidiAround(String string) {
		return BiDiUtils.BIDI_RLE + string + BiDiUtils.BIDI_PDF;
	}

	public static String addLtrBidiAround(String string) {
		return BiDiUtils.BIDI_LRE + string + BiDiUtils.BIDI_PDF;
	}

	public static boolean isSourceLangRtl() {
		return Language.isRTL(getSourceLanguage());
	}

	public static boolean isTargetLangRtl() {
		return Language.isRTL(getTargetLanguage());
	}

	public static String getSourceLanguage() {
		return Core.getProject().getProjectProperties().getSourceLanguage().getLanguageCode();
	}

	public static String getTargetLanguage() {
		return Core.getProject().getProjectProperties().getTargetLanguage().getLanguageCode();
	}

	public static boolean isLocaleRtl() {
		return Language.localeIsRTL();
	}
}
