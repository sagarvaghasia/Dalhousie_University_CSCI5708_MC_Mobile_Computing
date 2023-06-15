package com.example.group7project.model

class Match {
    var email: String? = "";
    var name: String? = "";
    var userType: String? = "";
    var lastMessage: String? = "";
    var isLastMessageByMatchedUser: Boolean? = true;
    var chatId: String? = "";
    var imageURL: String? = "";

    constructor() {}

    constructor(
        email: String,
        name: String,
        userType: String,
        lastMessage: String,
        isLastMessageByMatchedUser: Boolean,
        chatId: String,
        imageURL: String
    ) {
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.lastMessage = lastMessage;
        this.isLastMessageByMatchedUser = isLastMessageByMatchedUser;
        this.chatId = chatId;
        this.imageURL = imageURL;
    }

}
