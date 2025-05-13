package com.davon.library.event;

import com.davon.library.model.User;

public interface UserStatusListener {
    void onUserStatusChange(User user, String oldStatus, String newStatus);
}
