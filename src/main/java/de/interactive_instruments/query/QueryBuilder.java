/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.query;

import java.util.List;

/**
 * A simplified Query Builder derived from the SQL syntax.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface QueryBuilder {

    // Pagination?

    Selection select(String field);

    Selection select(List<String> fields);

    QueryBuilder from(String item);

    QueryBuilder count(long limit);

    /**
     * Use with count!
     *
     * @param pageStart
     * @return
     */
    QueryBuilder page(long pageStart);

    QueryBuilder joinOn(String item, String onFirstField, String onSecondField);

    QueryPredicate where(String attribute);

    QueryBuilder orderBy(String item);
}
