package com.android.internal.telephony;

import android.os.Bundle;

import java.util.List;

interface ITelephony {

     void answerRingingCall();

     boolean endCall();

     void silenceRinger();

     boolean showCallScreenWithDialpad(boolean showDialpad);

     boolean isVideoCall();
}