import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class ErrorFormatterService {

  constructor(
    private domSanitizer: DomSanitizer,
  ) {
  }

  format(error: any): string {
    let message = this.domSanitizer.sanitize(SecurityContext.HTML, error.error.message) ?? '';
    if (!!error.error.errors) {
      message += ':<ul>';
      for (const e of error.error.errors) {
        /* Use Angular's DomSanitizer to strip dangerous parts out of the HTML
         * before putting it into the error message.
         * Toastr already does this, but it can't hurt to do here too,
         * in case the library ever fails to do it.
         */
        const sanE = this.domSanitizer.sanitize(SecurityContext.HTML, e);
        message += `<li>${sanE}</li>`;
      }
      message += '</ul>';
    } else {
      message += '.';
    }
    return message;
  }

  logError(err: any): string {
    let errorMessage = 'An error occurred';
    if (err.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${err.error.message}`;
    } else if (err.error && typeof err.error === 'object') {
      const errorObject = err.error;
      if (errorObject.errors && Array.isArray(errorObject.errors)) {
        errorMessage = errorObject.errors.join(', ');
      } else if (errorObject.message) {
        errorMessage = errorObject.message;
      }
    } else {
      errorMessage = `Server-side error: ${err.statusText}`;
    }
    return errorMessage
  }

}
