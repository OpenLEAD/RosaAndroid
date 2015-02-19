package com.lead.rosa;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class DepthPicker extends DialogPreference {

	private NumberPicker depthPick;
	private int depth;
	private final int DEFAULT_DEPTH = 30;

	@Override
	protected View onCreateDialogView() {
		// TODO Auto-generated method stub
		View dialog = super.onCreateDialogView();
		depthPick = (NumberPicker) dialog.findViewById(R.id.align_limit);

		depthPick.setMaxValue(60);
		depthPick.setMinValue(5);
		depthPick.setValue(this.getPersistedInt(DEFAULT_DEPTH));
		depthPick.setWrapSelectorWheel(false);
		depthPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		
		return dialog;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInteger(index, DEFAULT_DEPTH);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue)
			depth = getPersistedInt(DEFAULT_DEPTH);
		else {
			depth = (Integer) defaultValue;
			persistInt(depth);
		}

		setSummary("O limite seguro de profundidate está configurado para "
				+ String.valueOf(depth) + "m.");

		super.onSetInitialValue(restorePersistedValue, defaultValue);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			depth = depthPick.getValue();
			persistInt(depth);
			setSummary("O limite seguro de profundidate está configurado para "
					+ String.valueOf(depth) + "m.");
		}
		super.onDialogClosed(positiveResult);
	}

	public DepthPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.depth_dialog);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		setDialogIcon(R.drawable.ic_menu_limite);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		// Check whether this Preference is persistent (continually saved)
		if (isPersistent()) {
			// No need to save instance state since it's persistent,
			// use superclass state
			return superState;
		}

		// Create instance of custom BaseSavedState
		final SavedState myState = new SavedState(superState);
		// Set the state's value with the class member that holds current
		// setting value
		myState.value = depth;
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// Check whether we saved the state in onSaveInstanceState
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save the state, so call superclass
			super.onRestoreInstanceState(state);
			return;
		}

		// Cast state to custom BaseSavedState and pass to superclass
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());

		// Set this Preference's widget to reflect the restored state
		depthPick.setValue(myState.value);
	}

	private static class SavedState extends BaseSavedState {
		// Member that holds the setting's value
		// Change this data type to match the type saved by your Preference
		int value;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			// Get the current preference's value
			value = source.readInt(); // Change this to read the appropriate
										// data type
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			// Write the preference's value
			dest.writeInt(value); // Change this to write the appropriate data
									// type
		}

		// Standard creator object using an instance of this class
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
