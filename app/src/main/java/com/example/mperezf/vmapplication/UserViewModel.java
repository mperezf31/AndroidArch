package com.example.mperezf.vmapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mperezf.vmapplication.db.AppDatabase;
import com.example.mperezf.vmapplication.db.UserModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class UserViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "UserViewModel";
    private final AppDatabase mDatabase;

    public ObservableBoolean mIsLoading = new ObservableBoolean(false);
    public ObservableBoolean mEnableButton = new ObservableBoolean(false);
    public ObservableField<String> mFirstName = new ObservableField<>();
    public ObservableField<String> mLastName = new ObservableField<>();
    public ObservableField<String> mNumUsers = new ObservableField<>();

    MutableLiveData<List<ItemViewModel>> mUserList = new MutableLiveData<>();
    MutableLiveData<String> mMsg = new MutableLiveData<>();

    private PublishSubject<String> mPublishFirstName = PublishSubject.create();
    private PublishSubject<String> mPublishLastName = PublishSubject.create();

    public UserViewModel(@NonNull Application application) {
        super(application);
        mDatabase = AppDatabase.getDatabase(getApplication());

        controlButtonAddUser();
    }

    /**
     * Enable/Disable button depends observable changes
     */
    private void controlButtonAddUser() {
        Observable.combineLatest(mPublishFirstName, mPublishLastName, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String firstName, String lastName) throws Exception {
                return !(firstName.isEmpty() || lastName.isEmpty());
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enable) throws Exception {
                mEnableButton.set(enable);
            }
        });
    }

    /**
     * Get all users from database after ON_RESUME event
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void showUsers() {
        mDatabase.getDao().getAllUserItems().toObservable().flatMap(new Function<List<UserModel>, ObservableSource<List<ItemViewModel>>>() {
            @Override
            public ObservableSource<List<ItemViewModel>> apply(List<UserModel> userModels) throws Exception {
                List<ItemViewModel> vms = new ArrayList<>();
                for (UserModel item : userModels) {
                    vms.add(new ItemViewModel(item));
                }
                return Observable.just(vms);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemViewModel>>() {
                    @Override
                    public void accept(List<ItemViewModel> itemViewModels) throws Exception {
                        mUserList.setValue(itemViewModels);
                        mNumUsers.set(String.valueOf(itemViewModels.size()) + " users saved");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mMsg.setValue("There was an error getting users");
                    }
                });

/*
        Observable.fromCallable(new Callable<List<ItemViewModel>>() {
            @Override
            public List<ItemViewModel> call() throws Exception {
                LiveData<List<UserModel>> users = mDatabase.getDao().getAllUserItems();
                List<ItemViewModel> vms = new ArrayList<>();
                if (users.getValue() == null) return vms;
                for (UserModel item : users.getValue()) {
                    vms.add(new ItemViewModel(item));
                }
                return vms;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<ItemViewModel>>() {
                    @Override
                    public void onNext(List<ItemViewModel> itemViewModels) {
                        mMsg.setValue(itemViewModels.size() + " users");
                        mUserList.setValue(itemViewModels);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMsg.setValue("There was an error getting users");
                    }

                    @Override
                    public void onComplete() {
                        Log.w(TAG, "onComplete get users");
                    }
                });*/
    }

    /**
     * Uptade username value, it's called directly from layout
     */
    public void onUserNameChanged(CharSequence s, int start, int before, int count) {
        Log.w(TAG, "onUserNameChanged");
        mFirstName.set(s.toString());
        mPublishFirstName.onNext(s.toString());
    }

    /**
     * Uptade lastname value, it's called directly from layout
     */
    public void onLastNameChanged(CharSequence s, int start, int before, int count) {
        Log.w(TAG, "onLastNameChanged");
        mLastName.set(s.toString());
        mPublishLastName.onNext(s.toString());
    }

    /**
     * Add a new user to room database
     */
    public void onAddUserClick() {
        //Show ProgressBar
        mIsLoading.set(true);
        Observable.zip(Observable.just(mFirstName.get()), Observable.just(mLastName.get()), new BiFunction<String, String, UserModel>() {
            @Override
            public UserModel apply(String firstName, String lastName) throws Exception {
                //Sleep thread for show progressbar 2 seconds
                Thread.sleep(2000);
                return new UserModel(firstName, lastName);
            }
        }).doOnNext(new Consumer<UserModel>() {
            @Override
            public void accept(UserModel userModel) throws Exception {
                mDatabase.getDao().addUser(userModel);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserModel>() {
                    @Override
                    public void accept(UserModel userModel) throws Exception {
                        //Hide ProgressBar
                        mIsLoading.set(false);
                        mFirstName.set("");
                        mLastName.set("");
                        mMsg.setValue("User inserted successfully");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Hide ProgressBar
                        mIsLoading.set(false);
                        mMsg.setValue("Can`t insert the user");
                    }
                });
    }
}
