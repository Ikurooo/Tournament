import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { HorseService } from '../../../service/horse.service';
import { Horse } from '../../../dto/horse';
import {NgIf} from "@angular/common";
import {DeletionResponseDto} from "../../../dto/deletion-response";
import {ToastrService} from "ngx-toastr";


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
  bannerError: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private horseService: HorseService,
    private router: Router,
    private notification: ToastrService
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
            this.bannerError = 'Could not fetch horses: ' + error.message;
            const errorMessage = error.status === 0
              ? 'Is the backend up?'
              : error.message.message;
            this.notification.error(errorMessage, 'Could Not Fetch Horses');
          }
        );
      }
    });
  }

  onDelete() {
    if (this.horseId) {
      this.horseService.deleteHorse(this.horseId).subscribe({
        next: (response: DeletionResponseDto) => {
          if (response.success) {
            console.log('Horse deleted successfully');
            this.router.navigate(['horses','deletion-successful']);
          } else {
            console.error('Error deleting horse:', response.message);
            this.router.navigate(['horses', 'deletion-failed']);
          }
        },
        error: (error) => {
          console.error('Unexpected error:', error);
          this.router.navigate(['horses', 'deletion-failed']);
        }
      });
    }
  }
}
