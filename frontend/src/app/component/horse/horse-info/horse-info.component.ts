import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { HorseService } from '../../../service/horse.service';
import { Horse } from '../../../dto/horse';
import {NgIf} from "@angular/common";


@Component({
  selector: 'app-horse-info',
  templateUrl: './horse-info.component.html',
  standalone: true,
  imports: [
    RouterLink,
    NgIf
  ],
  styleUrls: ['./horse-info.component.scss']
})
export class HorseInfoComponent implements OnInit {

  horseId: string | null = null;
  horse: Horse | null = null;

  constructor(private route: ActivatedRoute, private horseService: HorseService, private router: Router) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.horseId = params.get('id');
      if (this.horseId) {
        // Fetch horse details based on the ID
        this.horseService.getById(this.horseId).subscribe(
          (data: Horse) => {
            this.horse = data;
          },
          (error) => {
            console.error('Error fetching horse details:', error);
            // Handle error as needed
          }
        );
      }
    });
  }

  onDelete() {
    if (this.horseId) {
      // Call the service method to delete the horse
      this.horseService.deleteHorse(this.horseId).subscribe(
        () => {
          // After successful deletion, navigate to the deletion-successful route
          this.router.navigate(['/horses/deletion-successful']);
        },
        (error) => {
          console.error('Error deleting horse:', error);
          // Handle error as needed
        }
      );
    }
  }
}
