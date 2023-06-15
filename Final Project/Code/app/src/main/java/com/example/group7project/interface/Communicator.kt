package com.example.group7project.`interface`

import com.example.group7project.model.Meeting

interface Communicator {
    fun goToScheduleMeetingFragment()
    fun goBackFun()
    fun goToConfirmScheduleMeetingFragment(meetingModel: Meeting)
}