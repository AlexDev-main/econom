import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from '../shared/material/material.module';

import { UiButtonComponent } from './atoms/ui-button/ui-button.component';
import { LanguageSelectorComponent } from './molecules/language-selector/language-selector.component';
import { LanguageFooterComponent } from './organisms/language-footer/language-footer.component';

@NgModule({
  declarations: [
    UiButtonComponent,
    LanguageSelectorComponent,
    LanguageFooterComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    UiButtonComponent,
    LanguageFooterComponent
  ]
})
export class ComponentsModule {}