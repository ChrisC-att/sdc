/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

export class HierarchyDisplayOptions {
    idProperty: string;
    valueProperty: string;
    childrenProperty: string;
    searchText:string;
    archived:boolean;

    iconProperty: string;
    constructor(idProperty:string, valueProperty:string, childrenProperty?:string, searchText?:string, iconProperty?:string, archived?:boolean) {
    
        this.idProperty = idProperty;
        this.valueProperty = valueProperty;
        this.childrenProperty = childrenProperty;
        this.searchText = searchText;
        this.archived = archived;
        this.iconProperty = iconProperty;
    }
}
