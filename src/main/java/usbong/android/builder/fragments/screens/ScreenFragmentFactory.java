package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import usbong.android.builder.enums.UsbongBuilderScreenType;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class ScreenFragmentFactory {

    public static BaseScreenFragment getFragment(String screenType, Bundle arguments) {
        if (UsbongBuilderScreenType.TEXT.getName().equals(screenType)) {
            return TextDisplayScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.DECISION.getName().equals(screenType)) {
            return DecisionScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.IMAGE.getName().equals(screenType)) {
            return ImageFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.TEXT_AND_IMAGE.getName().equals(screenType)) {
            return TextImageFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.TEXT_INPUT.getName().equals(screenType)) {
            return TextInputScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.SPECIAL_INPUT.getName().equals(screenType)) {
            return SpecialInputScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.SEND.getName().equals(screenType)) {
            return SendScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.VIDEO.getName().equals(screenType)) {
            return VideoScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.MISC.getName().equals(screenType)) {
            return MiscScreenFragment.newInstance(arguments);
        } else if (UsbongBuilderScreenType.LIST.getName().equals(screenType)) {
            return ListScreenFragment.newInstance(arguments);
        }
        throw new IllegalArgumentException("unhandled screen type: " + screenType);
    }
}
