package uk.co.dajohnston.houseworkapi.userinfo.model;

public record UserInfoDTO(
    String email, String firstName, String lastName, String nickname, String picture) {}
