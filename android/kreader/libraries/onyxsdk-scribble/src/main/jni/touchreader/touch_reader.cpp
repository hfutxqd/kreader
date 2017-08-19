#include "com_onyx_android_sdk_scribble_touch_RawInputProcessor.h"
#include "log.h"
#include "JNIUtils.h"
#include "touch_reader.h"

#include <jni.h>
#include <iostream>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <errno.h>

TouchReader::TouchReader() {

}

TouchReader::~TouchReader() {
    closeDevice();
    if (limitArray) {
        free(limitArray);
        limitArray = NULL;
    }
}

void TouchReader::setStrokeWidth(float width) {
    strokeWidth = width;
}

bool TouchReader::inLimitRegion(float x, float y) {
    if (!limitArray) {
        return true;
    }

    float detectStorkeWidth = strokeWidth / 2;

    for (int i = 0; i < limitArrayLength; i += 4) {
        int leftLimit = limitArray[i];
        int topLimit = limitArray[i + 1];
        int rightLimit = limitArray[i + 2];
        int bottomLimit = limitArray[i + 3];

        if (leftLimit <= (x - detectStorkeWidth) &&
            (x + detectStorkeWidth) <= rightLimit &&
            topLimit <= (y - detectStorkeWidth) &&
            (y + detectStorkeWidth) <= bottomLimit) {
            return true;
        }
    }
    return false;
}

int TouchReader::openDevice(const std::string& devicePath, std::string& deviceName) {
    int version;
    char name[80] = {0};
    char location[80] = {0};
    char idstr[80] = {0};
    struct input_id id;

    fd = open(devicePath.c_str(), O_RDONLY);
    if(fd < 0) {
        LOGE("could not open %s, %s\n", devicePath.c_str(), strerror(errno));
        return -1;
    }

    if(ioctl(fd, EVIOCGVERSION, &version)) {
        LOGE("could not get driver version for %s, %s\n", devicePath.c_str(), strerror(errno));
        return -1;
    }
    if(ioctl(fd, EVIOCGID, &id)) {
        LOGE("could not get driver id for %s, %s\n", devicePath.c_str(), strerror(errno));
        return -1;
    }
    if(ioctl(fd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
        LOGE("could not get device name for %s, %s\n", devicePath.c_str(), strerror(errno));
        name[0] = '\0';
    }
    if(ioctl(fd, EVIOCGPHYS(sizeof(location) - 1), &location) < 1) {
        //LOGE("could not get location for %s, %s\n", devicePath.string(), strerror(errno));
        location[0] = '\0';
    }
    if(ioctl(fd, EVIOCGUNIQ(sizeof(idstr) - 1), &idstr) < 1) {
        //LOGE("could not get idstring for %s, %s\n", devicePath.string(), strerror(errno));
        idstr[0] = '\0';
    }

    deviceName = name;
    return fd;
}

void TouchReader::processEvent(onTouchPointReceived callback, int type, int code, int value, long ts) {
    if (type == EV_ABS) {
        if (code == ABS_X) {
            px = value;
        } else if (code == ABS_Y) {
            py = value;
        } else if (code == ABS_PRESSURE) {
            pressure = value;
        }
    } else if (type == EV_SYN) {
        int state;
        if (pressed) {
            if (!lastPressed) {
                if (inLimitRegion(px, py)) {
                    lastPressed = true;
                    state = ON_PRESS;
                } else {
                    state = ON_RELEASE;
                }
            } else {
                state = inLimitRegion(px, py) ? ON_MOVE : ON_RELEASE;
            }
        } else {
            state = ON_RELEASE;
        }
        callback(px, py, pressure, ts, erasing, state);
    } else if (type == EV_KEY) {
        if (code ==  BTN_TOUCH) {
            erasing = false;
            pressed = value;
            lastPressed = false;
        } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
            erasing = false;
        } else if (code == BTN_TOOL_RUBBER) {
            erasing = true;
            pressed = value;
        }
    }
}

void TouchReader::readTouchEventLoop(onTouchPointReceived callback){
    running = true;
    struct input_event event;
    while (running && fd > 0) {
        int res = read(fd, &event, sizeof(event));
        if (res < (int)sizeof(event)) {
            continue;
        }
        processEvent(callback, event.type, event.code, event.value, event.time.tv_usec);
    }
}

std::string TouchReader::findDevice() {
    for(int i = 0; i < 3; ++i) {
        std::string path = "/dev/input/event" + i;
        std::string deviceName;
        int fd = openDevice(path, deviceName);
        close(fd);
        if (debug) {
            LOGI("try path %s result name %s", path.c_str(), deviceName.c_str());
        }
        if (deviceName == "hanvon_tp") {
            return path;
        }
    }
    return "/dev/input/event1";
}

void TouchReader::closeDevice(){
    if (fd > 0) {
        close(fd);
        fd = 0;
    }
    running = false;
}

void TouchReader::setLimitRegion(int *array, int len) {
    if (limitArray) {
        free(limitArray);
    }
    limitArray = array;
    limitArrayLength = len;

    for (int i = 0; i < limitArrayLength; i += 4) {
        int left = limitArray[i];
        int top = limitArray[i + 1];
        int right = limitArray[i + 2];
        int bottom = limitArray[i + 3];

        int leftLimit = left <= right ? left : right;
        int topLimit = top <= bottom ? top : bottom;
        int rightLimit = right >= left ? right : left;
        int bottomLimit = bottom >= top ? bottom : top;

        limitArray[i] = leftLimit;
        limitArray[i + 1] = topLimit;
        limitArray[i + 2] = rightLimit;
        limitArray[i + 3] = bottomLimit;
    }
}

