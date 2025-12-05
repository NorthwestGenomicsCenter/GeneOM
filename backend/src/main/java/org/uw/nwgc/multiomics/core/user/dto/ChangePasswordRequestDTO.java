package org.uw.nwgc.multiomics.core.user.dto;

public record ChangePasswordRequestDTO(
   String username,
   String oldPassword,
   String newPassword
) {}