package org.store.authsvc.dtos.persistence;

import org.store.authsvc.dtos.response.IUserInfoDTO;

/** Internal projection used only while authenticating a user. */
public interface IUserLoginDTO extends IUserInfoDTO {

    String getPassword();
}
