package com.khokhlov.cloudstorage.facade;

/*
    Alternative for @AuthenticationPrincipal(expression = "id") long userId
*/

//@Component
//@RequiredArgsConstructor
//public class CurrentUserImpl implements CurrentUser {
//
//    @Override
//    public long getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
//            throw new AuthenticationCredentialsNotFoundException("Authentication required");
//
//        Object principal = auth.getPrincipal();
//        if (principal instanceof CustomUserDetails customUserDetails)
//            return customUserDetails.getId();
//
//        throw new AuthenticationCredentialsNotFoundException("Unexpected principal type");
//    }
//
//}