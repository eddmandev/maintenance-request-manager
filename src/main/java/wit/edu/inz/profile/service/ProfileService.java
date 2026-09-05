package wit.edu.inz.profile.service;

import api.model.ProfileResponse;

public interface ProfileService {

    ProfileResponse getProfile(String username);
}
