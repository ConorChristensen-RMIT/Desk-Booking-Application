package main.model;

/*
 * Singleton Class pattern used.
 * Holds profile to be accessed during duration of logged in session.
 */
public class ProfileHolder {

    private ProfileModel profile;

    private final static ProfileHolder INSTANCE = new ProfileHolder();

    private ProfileHolder() {};

    public static ProfileHolder getInstance(){
        return INSTANCE;
    }

    public void setProfile(ProfileModel p){
        this.profile = p;
    }

    public ProfileModel getProfile(){
        return this.profile;
    }
}
