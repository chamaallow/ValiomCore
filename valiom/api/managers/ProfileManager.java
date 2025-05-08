package com.valiom.api.managers;

import com.valiom.models.Profile;
import java.util.Optional;
import java.util.UUID;

public interface ProfileManager {

    void createProfile(UUID uuid);

    Profile getProfile(UUID uuid);

    Profile safeGetProfile(UUID uuid);

    void saveProfile(UUID uuid);

    void saveAllProfiles();

    void deleteProfile(UUID uuid);

    void updateRank(UUID uuid, String newRank);
}
