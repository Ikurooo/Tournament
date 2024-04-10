import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {HorseService} from '../../../service/horse.service';
import {Horse} from '../../../dto/horse';
import {DeletionResponseDto} from "../../../dto/deletion-response";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";


@Component({
  selector: 'app-horse-info',
  templateUrl: './horse-info.component.html',
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
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.horseId = params.get('id');
      if (this.horseId) {
        // Fetch horse details based on the ID
        this.horseService.getById(this.horseId).subscribe(
          (data: Horse) => {
            this.horse = data;
            this.horse.dateOfBirth = new Date(data.dateOfBirth);
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
            this.router.navigate(['horses', 'deletion-successful']);
          } else {
            this.notification.error("Failed deleting horse: " + response.message)
          }
        },
        error: (err) => {
          this.notification.error(this.errorFormatter.format(err), "Failed to fetch horse details.", {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }
}
