import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from '../shared/material/material.module';

import { UiButtonComponent } from './atoms/ui-button/ui-button.component';

@NgModule({
  declarations: [
    UiButtonComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    UiButtonComponent
  ]
})
export class ComponentsModule {}