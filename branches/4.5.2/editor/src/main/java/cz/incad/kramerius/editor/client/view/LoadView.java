/*
 * Copyright (C) 2010 Jan Pokorsky
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package cz.incad.kramerius.editor.client.view;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 *
 * @author Jan Pokorsky
 */
public interface LoadView {

    void hide();

    void setCallback(Callback c);

    void show();

    void showError(String s);

    HasValue<String> pid();
    
    HasValue<String> title();

    public interface Callback {
        void onLoadViewCommit(String input);
        void onLoadViewSuggestionCommit(SuggestOracle.Suggestion suggestion);
        void onLoadViewSuggestionRequest(SuggestOracle.Request request, SuggestOracle.Callback callback);
    }

}
