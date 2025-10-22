import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-side-bar-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './side-bar-component.html',
  styleUrls: ['./side-bar-component.css']
})
export class SideBarComponent {
  @Input() collapsed = false;
  @Output() toggleSidebarEvent = new EventEmitter<void>();

  toggleSidebar() {
    this.toggleSidebarEvent.emit();
  }
}
