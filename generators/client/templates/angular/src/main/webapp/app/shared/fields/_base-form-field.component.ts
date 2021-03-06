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
import { ControlValueAccessor } from '@angular/forms';

export class BaseFormField<T> implements ControlValueAccessor {
    private innerValue: T;
    protected disabled: boolean;

    private changed = new Array<(value: T) => void>();
    private touched = new Array<() => void>();

    public getValue(): T {
        return this.innerValue;
    }

    setValue(value: T) {
        if (this.innerValue !== value) {
            this.innerValue = value;
            this.changed.forEach((f) => f(value));
        }
    }

    forceOnChange() {
        this.changed.forEach((f) => f(this.innerValue));
    }

    touch() {
        this.touched.forEach((f) => f());
    }

    writeValue(value: T) {
        this.innerValue = value;
    }

    registerOnChange(fn: (value: T) => void) {
        this.changed.push(fn);
    }

    registerOnTouched(fn: () => void) {
        this.touched.push(fn);
    }

    setDisabledState(isDisabled: boolean) {
        this.disabled = isDisabled;
    }
}
