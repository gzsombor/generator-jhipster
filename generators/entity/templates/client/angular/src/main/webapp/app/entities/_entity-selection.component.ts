<%#
 Copyright 2013-2017 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see http://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
import { Component } from '@angular/core';
import { NG_VALUE_ACCESSOR } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { NgbTypeaheadConfig } from '@ng-bootstrap/ng-bootstrap';

import { ResponseWrapper, BaseFormField } from '../../shared';

import { <%= entityAngularName %> } from './<%= entityFileName %>.model';
import { <%= entityAngularName %>Service } from './<%= entityFileName %>.service';

@Component({
  selector: '<%= jhiPrefix %>-<%= entityFileName %>-selection',
  templateUrl: './<%= entityFileName %>-selection.component.html',
  providers: [
      {provide: NG_VALUE_ACCESSOR, useExisting: <%= entityAngularName %>SelectionComponent, multi: true},
      NgbTypeaheadConfig
    ],
})
export class <%= entityAngularName %>SelectionComponent extends BaseFormField<<%= entityAngularName %>> {
    searching = false;
    searchFailed = false;
    selected: <%= entityAngularName %>;
    hideSearchingWhenUnsubscribed = new Observable(() => () => this.searching = false);

    constructor(
        private config: NgbTypeaheadConfig,
        private <%= entityInstance %>Service: <%= entityAngularName %>Service
    ) {
        super();
        config.showHint = true;
    }

    search = (text$: Observable<string>) =>
        text$
          .debounceTime(300)
          .distinctUntilChanged()
          .do(() => this.searching = true)
          .switchMap((term) =>
              this.<%= entityInstance %>Service.query({}, {'name.contains': term})
                  .do(() => this.searchFailed = false)
                  .map((r: ResponseWrapper) => r.json)
                  .catch(() => {
                      this.searchFailed = true;
                      return Observable.of([]);
                  }))
           .do(() => this.searching = false)
           .merge(this.hideSearchingWhenUnsubscribed);

    formatter = (x: {name: string}) => x.name;

    onSelect($event, input) {
      const item = $event.item;
      this.setValue(item);
      $event.preventDefault();
      input.value = item.name;
    }

}
