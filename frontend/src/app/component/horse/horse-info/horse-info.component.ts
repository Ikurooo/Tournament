import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { HorseService } from '../../../service/horse.service';
import { Horse } from '../../../dto/horse';
import {NgIf} from "@angular/common";
import {DeletionResponseDto} from "../../../dto/deletion-response";


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

  constructor(
    private route: ActivatedRoute,
    private horseService: HorseService,
    private router: Router
  ) { }

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
    console.log('onDelete called');
    if (this.horseId) {
      this.horseService.deleteHorse(this.horseId).subscribe({
        next: (response: DeletionResponseDto) => {
          if (response.success) {
            console.log('Horse deleted successfully');
            this.router.navigate(['horses','deletion-successful']);
          } else {
            console.error('Error deleting horse:', response.message);
            // Handle the error message or navigate as needed
            this.router.navigate(['horses', 'deletion-failed']);
          }
        },
        error: (error) => {
          console.error('Unexpected error:', error);
          // Handle other errors if needed
          this.router.navigate(['horses', 'deletion-failed']);
        }
      });
    }
  }
}
