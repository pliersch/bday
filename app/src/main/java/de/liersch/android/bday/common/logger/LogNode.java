/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.liersch.android.bday.common.logger;

/**
 * Simple {@link ILogNode} filter, removes everything except the message.
 * Useful for situations like on-screen log output where you don't want a lot of metadata displayed,
 * just easy-to-read message updates as they're happening.
 */
public class LogNode implements ILogNode {

    ILogNode mNext;

    /**
     * Takes the "next" ILogNode as a parameter, to simplify chaining.
     *
     * @param next The next ILogNode in the pipeline.
     */
    public LogNode(ILogNode next) {
        mNext = next;
    }

    public LogNode() {
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        if (mNext != null) {
            getNext().println(Log.NONE, tag, msg, null);
        }
    }

    /**
     * Returns the next ILogNode in the chain.
     */
    public ILogNode getNext() {
        return mNext;
    }

    /**
     * Sets the ILogNode data will be sent to..
     */
    public void setNext(ILogNode node) {
        mNext = node;
    }

}
