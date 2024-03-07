import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';

@Component({
    selector: 'app-horse-deletion-failed',
    templateUrl: './horse-deletion-failed.component.html',
    standalone: true,
    imports: [
        RouterOutlet
    ],
    styleUrls: ['./horse-deletion-failed.component.scss']
})
export class HorseDeletionFailedComponent {

    constructor(private router: Router) { }

    navigateToHorses(): void {
        this.router.navigate(['/horses']);
    }
}
