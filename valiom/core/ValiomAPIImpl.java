package com.valiom.core;

import com.valiom.ValiomCore;
import com.valiom.api.ValiomAPI;
import com.valiom.api.managers.ProfileManager;
import com.valiom.core.managers.ProfileManagerImpl;

public class ValiomAPIImpl implements ValiomAPI {

    private final ProfileManager profileManager;

    public ValiomAPIImpl(ValiomCore valiomCore) {
        this.profileManager = new ProfileManagerImpl();
    }

    @Override
    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
