package dem.xbitly.eventplatform.ui.messanger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessengerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MessengerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is messenger fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}