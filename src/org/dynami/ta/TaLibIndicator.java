/*
 * Copyright 2015 Alessandro Atria - a.atria@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dynami.ta;

import org.dynami.core.ITechnicalIndicator;

import com.tictactec.ta.lib.Core;

public abstract class TaLibIndicator implements ITechnicalIndicator{
	protected static final Core core = new Core();
	protected boolean ready = false;
	protected int lastLength = 0;
	protected int PAD = 10;

	public void reset(){
		lastLength = 0;
		ready = false;
		series().forEach(s->s.get().clear());
	}
}
