import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-horse-deleted',
  templateUrl: './horse-deleted.component.html',
  standalone: true,
  styleUrls: ['./horse-deleted.component.scss']
})
export class HorseDeletedComponent {

  constructor(private router: Router) { }

  navigateToHorses(): void {
    this.router.navigate(['/horses']);
  }
}
