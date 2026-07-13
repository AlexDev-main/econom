import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ThemePalette } from '@angular/material/core';

export type ButtonType = 'flat' | 'stroked';

@Component({
  selector: 'app-ui-button',
  templateUrl: './ui-button.component.html',
  styleUrls: ['./ui-button.component.scss']
})
export class UiButtonComponent {

  @Input() type: 'button' | 'submit' = 'button';

  @Input() appearance: ButtonType = 'flat';

  @Input() color: ThemePalette = 'primary';

  @Input() disabled = false;

  @Input() loading = false;

  @Output() clicked = new EventEmitter<void>();

  onClick(): void {

    if (!this.disabled && !this.loading) {
      this.clicked.emit();
    }

  }

}