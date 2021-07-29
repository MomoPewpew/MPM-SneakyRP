package aurelienribon.tweenengine;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

/**
 * Collection of miscellaneous utilities.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenUtils {
	//why are java enums so terrible, this is the best way I could find to do int enums in java
	public static final int LINEAR_INOUT = 0;
	public static final int QUAD_IN =      1;
	public static final int QUAD_OUT =     2;
	public static final int QUAD_INOUT =   3;
	public static final int CUBIC_IN =     4;
	public static final int CUBIC_OUT =    5;
	public static final int CUBIC_INOUT =  6;
	public static final int QUART_IN =     7;
	public static final int QUART_OUT =    8;
	public static final int QUART_INOUT =  9;
	public static final int QUINT_IN =     10;
	public static final int QUINT_OUT =    11;
	public static final int QUINT_INOUT =  12;
	public static final int CIRC_IN =      13;
	public static final int CIRC_OUT =     14;
	public static final int CIRC_INOUT =   15;
	public static final int SINE_IN =      16;
	public static final int SINE_OUT =     17;
	public static final int SINE_INOUT =   18;
	public static final int EXPO_IN =      19;
	public static final int EXPO_OUT =     20;
	public static final int EXPO_INOUT =   21;
	public static final int BACK_IN =      22;
	public static final int BACK_OUT =     23;
	public static final int BACK_INOUT =   24;
	public static final int BOUNCE_IN =    25;
	public static final int BOUNCE_OUT =   26;
	public static final int BOUNCE_INOUT = 27;
	public static final int ELASTIC_IN =   28;
	public static final int ELASTIC_OUT =  29;
	public static final int ELASTIC_INOUT = 30;

	public static TweenEquation[] easings = new TweenEquation[] {
		Linear.INOUT,
		Quad.IN, Quad.OUT, Quad.INOUT,
		Cubic.IN, Cubic.OUT, Cubic.INOUT,
		Quart.IN, Quart.OUT, Quart.INOUT,
		Quint.IN, Quint.OUT, Quint.INOUT,
		Circ.IN, Circ.OUT, Circ.INOUT,
		Sine.IN, Sine.OUT, Sine.INOUT,
		Expo.IN, Expo.OUT, Expo.INOUT,
		Back.IN, Back.OUT, Back.INOUT,
		Bounce.IN, Bounce.OUT, Bounce.INOUT,
		Elastic.IN, Elastic.OUT, Elastic.INOUT
	};

	/**
	 * Takes an easing name and gives you the corresponding TweenEquation.
	 * You probably won't need this, but tools will love that.
	 *
	 * @param easingName The name of an easing, like "Quad.INOUT".
	 * @return The parsed equation, or null if there is no match.
	 */
	public static TweenEquation parseEasing(String easingName) {
		for (TweenEquation easing : easings) {
			if (easingName.toLowerCase().equals(easing.toString().toLowerCase()))
				return easing;
		}

		return null;
	}
	public static int parseEasingToEnum(String easingName) {

		for(int i = 0; i < easings.length; i++) {
			if (easingName.toLowerCase().equals(easings[i].toString().toLowerCase()))
				return i;
		}

		return -1;
	}
}
