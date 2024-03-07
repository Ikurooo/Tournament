import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-horse-deleted',
  templateUrl: './horse-deleted.component.html',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  styleUrls: ['./horse-deleted.component.scss']
})
export class HorseDeletedComponent {

  constructor(private router: Router) { }

  navigateToHorses(): void {
    this.router.navigate(['/horses']);
  }
}
